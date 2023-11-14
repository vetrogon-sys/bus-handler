package com.example.busticketplatform.scunners.crawler;

import com.example.busticketplatform.scunners.SiteCrawler;
import com.example.busticketplatform.scunners.CrawlerConfig;
import com.example.busticketplatform.serialize.TaskSerializer;
import com.example.busticketplatform.web.HttpResponse;
import com.example.busticketplatform.web.link.LinkBuilder;
import com.example.busticketplatform.web.services.RestService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class AtlasSiteCrawler extends SiteCrawler {

    public static final String DOMAIN = "atlasbus.by";
    public static final String API_ORIGIN = "https://%s/api".formatted(DOMAIN);
    public static final String ORIGIN = "https://%s".formatted(DOMAIN);

    public AtlasSiteCrawler(RestService restService, TaskSerializer taskSerializer) {
        super(new CrawlerConfig(restService, taskSerializer)
              .setLimitRequests(1)
              .setUnitCount(1)
              .setMaxUnitWorkingTime(TimeUnit.MINUTES.toMillis(5))
              .setPauseRequest(500)
        );
    }

    @Override
    public void handleStart() {
        addTask(new LinkBuilder(API_ORIGIN + "/search/suggest?user_input=&from_id=&to_id=&locale=ru").build())
              .success(this::handleAvailableCities);
    }

    private void handleAvailableCities(HttpResponse response) {
        for (JsonNode cityNode : response.json()) {
            String id = cityNode.get("id").asText();
            String name = cityNode.get("name").asText();
        }
    }

}
