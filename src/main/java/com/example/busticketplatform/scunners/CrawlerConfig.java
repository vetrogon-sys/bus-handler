package com.example.busticketplatform.scunners;

import com.example.busticketplatform.serialize.Source;
import com.example.busticketplatform.serialize.TaskSerializer;
import com.example.busticketplatform.web.services.proxy.CheckProxySettings;
import com.example.busticketplatform.web.services.proxy.ProxyService;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Getter
@Component
public final class CrawlerConfig {
    private final ProxyService proxyService;
    private final TaskSerializer taskSerializer;
    private Source source;
    private AtomicLong maxUnitWorkingTime;
    private AtomicLong meaningfulRestartTime;
    private long pauseRequest;
    private TimeUnit pauseUnitType;
    private CheckProxySettings checkProxySettings;
    private int limitRequests;
    private int unitCount;
    private boolean restart;

    public CrawlerConfig(ProxyService proxyService, TaskSerializer taskSerializer) {
        this.proxyService = proxyService;
        this.taskSerializer = taskSerializer;
        this.maxUnitWorkingTime = new AtomicLong(ModelConstants.FIVE_MINUTES);
        this.meaningfulRestartTime = new AtomicLong(ModelConstants.TEN_MINUTES);
        this.pauseRequest = 100L;
        this.pauseUnitType = TimeUnit.MILLISECONDS;
        this.limitRequests = 1;
        this.unitCount = 1;
        this.restart = false;
    }

    public CrawlerConfig setMaxUnitWorkingTime(long maxUnitWorkingTime) {
        this.maxUnitWorkingTime = new AtomicLong(maxUnitWorkingTime);
        return this;
    }

    public CrawlerConfig setMeaningfulRestartTime(long meaningfulRestartTime) {
        this.meaningfulRestartTime = new AtomicLong(meaningfulRestartTime);
        return this;
    }

    public CrawlerConfig setPauseRequest(long pauseRequest, TimeUnit timeUnit) {
        if (pauseRequest > 1) {
            this.pauseRequest = pauseRequest;
            this.pauseUnitType = timeUnit;
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

    public CrawlerConfig setSource(Source source) {
        this.source = source;
        return this;
    }

    public CrawlerConfig setRestart(boolean restart) {
        this.restart = restart;
        return this;
    }

    public CrawlerConfig setCheckProxySettings(CheckProxySettings checkProxySettings) {
        this.checkProxySettings = checkProxySettings;
        return this;
    }

}
