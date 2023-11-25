package com.example.busticketplatform.scunners.crawler.bus;

import com.example.busticketplatform.scunners.CrawlerConfig;
import com.example.busticketplatform.scunners.SiteCrawler;
import com.example.busticketplatform.scunners.Task;
import com.example.busticketplatform.scunners.TaskState;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static com.example.busticketplatform.scunners.TaskState.*;
import static com.example.busticketplatform.scunners.TaskState.created;
import static org.slf4j.LoggerFactory.getLogger;

public abstract class TaskCollectorCrawler extends SiteCrawler {
    private static final Logger log = getLogger(TaskCollectorCrawler.class);

    private final Map<String, Task> tasks = new ConcurrentHashMap<>();
    private final Map<String, Task> currentTasks = new ConcurrentHashMap<>();

    private final Map<TaskState, AtomicLong> statesStatistics = new ConcurrentHashMap<>();;

    public TaskCollectorCrawler(CrawlerConfig config) {
        super(config);
    }

    protected void preStartScan() {
        if (source == null) {
            throw new RuntimeException("Unknown source value");
        }
        Map<String, Task> restoredTasks = taskSerializer.readTasks(source);
        tasks.putAll(restoredTasks);

        Arrays.stream(values())
                .forEach(state -> statesStatistics.put(state, new AtomicLong()));

        incompleteTasks.forEach(task -> queueTasks.put(task.getUrl(), task));
        incompleteTasks.clear();
    }

    protected void beforeWriteState() {
        StringBuilder message = new StringBuilder();
        setInfoMessage(message);
        log.info(message.toString());
        taskSerializer.storeTasks(tasks, source);
    }

    protected void putTask(Task task) {
        increment(collected);

        if (tasks.containsKey(task.getId())) {
            increment(updated);
        } else {
            increment(created);
            tasks.put(task.getId(), task);
        }
        currentTasks.put(task.getId(), task);
    }

    public void appendTasksStatistics(StringBuilder sb) {
        statesStatistics.forEach((key, value) -> {
            sb.append(key).append(": ").append(value);
            appendNewLine(sb);
        });
    }

    public void setInfoMessage(StringBuilder message) {
        appendNewLine(message);
        message.append("==Tasks==");
        appendNewLine(message);
        message.append("All tasks: ").append(tasks.size());
        appendNewLine(message);
        appendTasksStatistics(message);
    }

    public void appendNewLine(StringBuilder sb) {
        sb.append("\n");
    }

    public long increment(TaskState state) {
        return statesStatistics.get(state)
                .incrementAndGet();
    }

}
