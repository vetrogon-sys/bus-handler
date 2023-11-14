package com.example.busticketplatform.scunners;

import com.example.busticketplatform.serialize.TaskSerializer;
import com.example.busticketplatform.web.services.RestService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Getter
@Component
public final class CrawlerConfig {
    public static final long ONE_MINUTE = TimeUnit.MINUTES.toMillis(1);
    public static final long FIVE_MINUTES = TimeUnit.MINUTES.toMillis(5);
    public static final long TEN_MINUTES = TimeUnit.MINUTES.toMillis(10);

    private final RestService restService;
    private final TaskSerializer taskSerializer;
    private AtomicLong maxUnitWorkingTime;
    private AtomicLong meaningfulRestartTime;
    private long pauseRequest;
    private int limitRequests;
    private int unitCount;

    public CrawlerConfig(RestService restService, TaskSerializer taskSerializer) {
        this.restService = restService;
        this.taskSerializer = taskSerializer;
        this.maxUnitWorkingTime = new AtomicLong(FIVE_MINUTES);
        this.meaningfulRestartTime = new AtomicLong(TEN_MINUTES);
        this.pauseRequest = 1L;
        this.limitRequests = 1;
        this.unitCount = 1;
    }

    public CrawlerConfig setMaxUnitWorkingTime(long maxUnitWorkingTime) {
        this.maxUnitWorkingTime = new AtomicLong(maxUnitWorkingTime);
        return this;
    }

    public CrawlerConfig setMeaningfulRestartTime(long meaningfulRestartTime) {
        this.meaningfulRestartTime = new AtomicLong(meaningfulRestartTime);
        return this;
    }

    public CrawlerConfig setPauseRequest(long pauseRequest) {
        if (pauseRequest > 1) {
            this.pauseRequest = pauseRequest;
        }
        return this;
    }

    public CrawlerConfig setLimitRequests(int limitRequests) {
        if (limitRequests > 0) {
            this.limitRequests = limitRequests;
        }
        return this;
    }

    public CrawlerConfig setUnitCount(int unitCount) {
        if (unitCount > 0) {
            this.unitCount = unitCount;
        }
        return this;
    }

}
