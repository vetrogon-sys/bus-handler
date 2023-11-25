package com.example.busticketplatform.scunners.model;

import com.example.busticketplatform.scunners.model.config.CrawlerConfig;
import com.example.busticketplatform.scunners.model.entity.CrawlerTask;
import com.example.busticketplatform.serialize.Source;
import com.example.busticketplatform.serialize.TaskSerializer;
import com.example.busticketplatform.web.HttpResponse;
import com.example.busticketplatform.web.link.Link;
import com.example.busticketplatform.web.services.RestService;
import com.example.busticketplatform.web.services.proxy.CheckProxySettings;
import com.example.busticketplatform.web.services.proxy.ProxyService;
import lombok.Getter;
import org.slf4j.Logger;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static org.slf4j.LoggerFactory.getLogger;

public abstract class SiteCrawler implements CollectorCrawler {
    private static final Logger log = getLogger(SiteCrawler.class);

    private final ProxyService proxyService;
    protected final CheckProxySettings checkProxySettings;
    protected final TaskSerializer taskSerializer;
    protected final Source source;
    protected final BlockingQueue<CrawlerTask<?>> runningTasks;
    protected final Map<String, CrawlerTask<?>> queueTasks;
    protected final Queue<CrawlerTask<?>> incompleteTasks;
    protected final Map<String, CrawlerTask<?>> cachedTasks;

    public final int unitCount;
    public final int limitOfRequest;

    private final long pauseRequest;
    private final AtomicLong startWorkingTime;
    private final AtomicLong endWorkingTime;
    private final AtomicLong maxUnitWorkingTime;
    private final AtomicLong restartTime;
    private final AtomicLong lastMessageTime;

    @Getter
    private final AtomicBoolean isWorking;

    private ScheduledExecutorService unitExecutorService;

    private final boolean isRestartAfterFail;

    public SiteCrawler(CrawlerConfig config) {
        this.taskSerializer = config.getTaskSerializer();
        this.source = config.getSource();
        this.unitCount = config.getUnitCount();
        this.limitOfRequest = config.getLimitRequests();
        this.restartTime = config.getMeaningfulRestartTime();
        this.maxUnitWorkingTime = config.getMaxUnitWorkingTime();
        this.pauseRequest = config.getPauseRequest();
        this.isRestartAfterFail = config.isRestart();
        this.proxyService = config.getProxyService();
        this.checkProxySettings = config.getCheckProxySettings();
        this.startWorkingTime = new AtomicLong();
        this.endWorkingTime = new AtomicLong();
        this.lastMessageTime = new AtomicLong();
        this.isWorking = new AtomicBoolean();

        this.runningTasks = new ArrayBlockingQueue<>(this.limitOfRequest * this.unitCount);
        this.queueTasks = new ConcurrentHashMap<>();
        this.cachedTasks = new ConcurrentHashMap<>();
        this.incompleteTasks = new ConcurrentLinkedQueue<>();
    }

    public void run() {
        RestService restService;
        if (checkProxySettings != null) {
            restService = proxyService.buildTemplateWithProxy(checkProxySettings);
            if (restService == null) {
                log.warn("Can't find proxy");
                return;
            }
        } else {
            restService = proxyService.buildTemplateWithoutProxy();
        }
        log.info("Start crawle data {}", Thread.currentThread());
        startWorkingTime.set(System.currentTimeMillis());
        lastMessageTime.set(0);
        preStartScan();
        handleStart();

        this.unitExecutorService = Executors.newScheduledThreadPool(unitCount);
        unitExecutorService.scheduleAtFixedRate(() -> {

            fillTasksIfNeed();
            ExecutorService requestExecutor = Executors.newCachedThreadPool();
            for (int i = 0; i < limitOfRequest; i++) {
                requestExecutor.submit(() -> {
                    CrawlerTask<?> task = runningTasks.poll();
                    if (task == null) {
                        return;
                    }
                    HttpResponse taskResponse = restService.execute(task.getLink());

                    if (!taskResponse.getStatus().is2xxSuccessful()) {
                        log.warn("Exeption via processing task {} status {}", task, taskResponse.getStatus());
                        if (isRestartAfterFail) {
                            queueTasks.put(task.getUrl(), task);
                        }
                    } else {
                        log.debug("Handle response {}", taskResponse);
                        lastMessageTime.set(System.currentTimeMillis());
                        cachedTasks.put(task.getUrl(), task);
                        task.getPostProcess().accept(taskResponse);
                    }
                });
            }

            if ((runningTasks.isEmpty() && queueTasks.isEmpty() && isLastMessageLongWait()) || needToEnd()) {
                unitExecutorService.shutdown();
                incompleteTasks.addAll(queueTasks.values());
                queueTasks.clear();
                cachedTasks.clear();
                beforeWriteState();
                endWorkingTime.set(System.currentTimeMillis());
                if (needToEnd()) {
                    log.info("Exit by unit timeout {}", maxUnitWorkingTime.get());
                }
                log.info("End scan: " + endWorkingTime.get());
            }
        }, 0L, pauseRequest, TimeUnit.MILLISECONDS);

    }

    private boolean isLastMessageLongWait() {
        return lastMessageTime.get() > 0 && lastMessageTime.get() + TimeUnit.SECONDS.toMillis(10) < System.currentTimeMillis();
    }

    protected abstract void preStartScan();


    protected abstract void beforeWriteState();

    public boolean needToRun() {
        return startWorkingTime.get() == 0
              || (endWorkingTime.get() + restartTime.get() < System.currentTimeMillis() && endWorkingTime.get() > startWorkingTime.get());
    }

    private boolean needToEnd() {
        return startWorkingTime.get() + maxUnitWorkingTime.get() < System.currentTimeMillis();
    }

    private void fillTasksIfNeed() {
        if (!cachedTasks.isEmpty()) {
            cachedTasks.keySet().forEach(queueTasks::remove);
        }

        if (runningTasks.size() < limitOfRequest && !queueTasks.isEmpty()) {
            for (int i = runningTasks.size(); i < limitOfRequest * unitCount; i++) {
                CrawlerTask<?> task = queueTasks.remove(queueTasks.keySet().stream().findFirst().orElse(""));
                if (task == null) {
                    break;
                }
                runningTasks.add(task);
            }
        }
    }

    protected CrawlerTask<?> addTask(Link link) {
        CrawlerTask<?> crawlerTask = CrawlerTask.builder()
              .link(link)
              .build();
        queueTasks.put(crawlerTask.getUrl(), crawlerTask);
        return crawlerTask;
    }

}
