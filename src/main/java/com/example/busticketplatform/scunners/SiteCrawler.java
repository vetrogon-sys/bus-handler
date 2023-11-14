package com.example.busticketplatform.scunners;

import com.example.busticketplatform.web.HttpResponse;
import com.example.busticketplatform.web.link.Link;
import com.example.busticketplatform.web.services.RestService;
import lombok.Getter;
import org.slf4j.Logger;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static org.slf4j.LoggerFactory.getLogger;

public abstract class SiteCrawler implements CollectorCrawler {
    private static final Logger log = getLogger(SiteCrawler.class);

    private final RestService restService;
    protected final BlockingQueue<CrawlerTask<?>> tasks;
    private final Map<String, CrawlerTask<?>> queueTasks;
    private final Queue<CrawlerTask<?>> uncomplitedTasks;
    private final Map<String, CrawlerTask<?>> cachedTasks;

    public final int unitCount;
    public final int limitOfRequest;

    private final long pauseRequest;
    private final AtomicLong startWorkingTime;
    private final AtomicLong endWorkingTime;
    private final AtomicLong maxUnitWorkingTime;
    private final AtomicLong restartTime;

    @Getter
    private final AtomicBoolean isWorking;

    private final Map<String, Task> currentTasks = new ConcurrentHashMap<>();

    public SiteCrawler(CrawlerConfig config) {
        this.restService = config.getRestService();
        this.unitCount = config.getUnitCount();
        this.limitOfRequest = config.getLimitRequests();
        this.restartTime = config.getMeaningfulRestartTime();
        this.maxUnitWorkingTime = config.getMaxUnitWorkingTime();
        this.pauseRequest = config.getPauseRequest();
        this.startWorkingTime = new AtomicLong();
        this.endWorkingTime = new AtomicLong();
        this.isWorking = new AtomicBoolean();

        this.tasks = new ArrayBlockingQueue<>(this.limitOfRequest);
        this.queueTasks = new ConcurrentHashMap<>();
        this.cachedTasks = new ConcurrentHashMap<>();
        this.uncomplitedTasks = new ConcurrentLinkedQueue<>();
    }

    public void run() {
        log.info("Start crawle data {}", Thread.currentThread());
        startWorkingTime.set(System.currentTimeMillis());
        preStartScan();
        handleStart();

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(unitCount);
        executorService.scheduleWithFixedDelay(() -> {
            fillTasksIfNeed();

            ScheduledExecutorService requestExecutor = Executors.newScheduledThreadPool(limitOfRequest);
            requestExecutor.scheduleWithFixedDelay(() -> {
                CrawlerTask<?> task = tasks.poll();
                if (task == null) {
                    return;
                }
                HttpResponse taskResponse = restService.execute(task.getLink());

                if (!taskResponse.getStatus().is2xxSuccessful()) {
                    log.warn("Exeption via processing task {} status {} wll restart", task, taskResponse.getStatus());
                    queueTasks.put(task.getUrl(), task);
                } else {
                    cachedTasks.put(task.getUrl(), task);
                    task.getPostProcess().accept(taskResponse);
                }
                requestExecutor.shutdown();
            }, 0L, pauseRequest, TimeUnit.MILLISECONDS);


            if ((tasks.isEmpty() && queueTasks.isEmpty()) || needToEnd()) {
                isWorking.set(false);
                endWorkingTime.set(System.currentTimeMillis());
                uncomplitedTasks.addAll(queueTasks.values());
                log.info("End scan: " + endWorkingTime.get());
                log.info("Collected task: " + cachedTasks.size());
                queueTasks.clear();
                cachedTasks.clear();
                executorService.shutdown();
            }
        }, 0L, pauseRequest, TimeUnit.MILLISECONDS);

    }

    protected void preStartScan() {
        uncomplitedTasks.forEach(task -> queueTasks.put(task.getUrl(), task));
        uncomplitedTasks.clear();
    }

    public boolean needToRun() {
        return endWorkingTime.get() + restartTime.get() < System.currentTimeMillis() && !isWorking.get();
    }

    private boolean needToEnd() {
        return startWorkingTime.get() + maxUnitWorkingTime.get() < System.currentTimeMillis();
    }

    private void fillTasksIfNeed() {
        if (!cachedTasks.isEmpty()) {
            cachedTasks.keySet().forEach(queueTasks::remove);
        }

        if (tasks.size() < limitOfRequest && !queueTasks.isEmpty()) {
            for (int i = tasks.size(); i < limitOfRequest; i++) {
                CrawlerTask<?> task = queueTasks.remove(queueTasks.keySet().stream().findFirst().orElse(""));
                if (task == null) {
                    break;
                }
                tasks.add(task);
            }
        }
    }

    protected void putTask(Task task) {
        currentTasks.put(task.getId(), task);
    }

    protected CrawlerTask<?> addTask(Link link) {
        CrawlerTask<?> crawlerTask = CrawlerTask.builder()
              .link(link)
              .build();
        queueTasks.put(crawlerTask.getUrl(), crawlerTask);
        return crawlerTask;
    }

    public String getStringName() {
        return "%s-%s".formatted(getClass().toString(), unitCount);
    }

}
