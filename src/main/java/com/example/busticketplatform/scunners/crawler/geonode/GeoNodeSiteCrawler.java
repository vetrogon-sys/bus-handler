package com.example.busticketplatform.scunners.crawler.geonode;

import com.example.busticketplatform.scunners.CrawlerConfig;
import com.example.busticketplatform.scunners.ModelConstants;
import com.example.busticketplatform.scunners.Task;
import com.example.busticketplatform.scunners.crawler.bus.TaskCollectorCrawler;
import com.example.busticketplatform.serialize.Source;
import com.example.busticketplatform.serialize.TaskSerializer;
import com.example.busticketplatform.web.HttpResponse;
import com.example.busticketplatform.web.link.LinkBuilder;
import com.example.busticketplatform.web.services.proxy.ProxyService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class GeoNodeSiteCrawler extends TaskCollectorCrawler {
    public static final String DOMAIN = "geonode.com";
    public static final String API_ORIGIN = "https://proxylist.%s/api".formatted(DOMAIN);
    public static final String ORIGIN = "https://%s".formatted(DOMAIN);

    public GeoNodeSiteCrawler(ProxyService proxyService, TaskSerializer taskSerializer) {
        super(new CrawlerConfig(proxyService, taskSerializer)
              .setUnitCount(2)
              .setLimitRequests(3)
              .setPauseRequest(500L, TimeUnit.MILLISECONDS)
              .setMaxUnitWorkingTime(ModelConstants.FIVE_MINUTES)
              .setMeaningfulRestartTime(ModelConstants.TEN_MINUTES)
              .setSource(Source.geonode)
        );
    }

    @Override
    public void handleStart() {
        addTask(new LinkBuilder(API_ORIGIN + "/proxy-list?limit=100&page=1&sort_by=lastChecked&sort_type=desc")
              .addHeader("referer", ORIGIN)
              .addHeader("origin", ORIGIN)
              .build())
              .success(this::handleProxies);
    }

    private void handleProxies(HttpResponse response) {
        for (JsonNode proxyNode : response.json().at("/data")) {
            String id = proxyNode.get("_id").asText();
            String ip = proxyNode.get("ip").asText();
            String port = proxyNode.get("port").asText();
            String country = proxyNode.get("country").asText();
            String protocol = proxyNode.at("/protocols").get(0).asText();

            putTask(new Task(id)
                  .param(ModelConstants.PROXY_IP, ip)
                  .param(ModelConstants.PROXY_PORT, port)
                  .param(ModelConstants.PROXY_COUNTRY, country)
                  .param(ModelConstants.PROXY_PROTOCOL, protocol)
            );
        }
    }
}
