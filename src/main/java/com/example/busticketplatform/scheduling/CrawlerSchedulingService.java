package com.example.busticketplatform.scheduling;

import com.example.busticketplatform.scunners.SiteCrawler;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CrawlerSchedulingService {

    final List<SiteCrawler> collectorCrawlers;

    @Scheduled(fixedDelay = 1000)
    public void schedule() {
        collectorCrawlers.parallelStream()
              .filter(SiteCrawler::needToRun)
              .forEach(SiteCrawler::run);
    }


}
