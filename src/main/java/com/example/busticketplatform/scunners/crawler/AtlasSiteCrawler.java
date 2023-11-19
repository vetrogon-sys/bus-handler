package com.example.busticketplatform.scunners.crawler;

import com.example.busticketplatform.scunners.CrawlerConfig;
import com.example.busticketplatform.scunners.ModelConstants;
import com.example.busticketplatform.scunners.SiteCrawler;
import com.example.busticketplatform.scunners.Task;
import com.example.busticketplatform.serialize.Source;
import com.example.busticketplatform.serialize.TaskSerializer;
import com.example.busticketplatform.web.HttpResponse;
import com.example.busticketplatform.web.link.Link;
import com.example.busticketplatform.web.link.LinkBuilder;
import com.example.busticketplatform.web.services.proxy.CheckProxySettings;
import com.example.busticketplatform.web.services.proxy.ProxyService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class AtlasSiteCrawler extends SiteCrawler {

    public static final String DOMAIN = "atlasbus.by";
    public static final String API_ORIGIN = "https://%s/api".formatted(DOMAIN);
    public static final String ORIGIN = "https://%s".formatted(DOMAIN);

    private final Map<String, String> availableCities = new ConcurrentHashMap<>();

    public static final int COLLECTED_DAYS_COUNT = 3;

    public static final CheckProxySettings CHECK_PROXY_SETTINGS = new CheckProxySettings("Атлас | Удобная покупка билетов на маршрутки и автобусы по всей Беларуси")
          .link(ORIGIN);

    public AtlasSiteCrawler(ProxyService proxyService, TaskSerializer taskSerializer) {
        super(new CrawlerConfig(proxyService, taskSerializer)
              .setUnitCount(1)
              .setCheckProxySettings(CHECK_PROXY_SETTINGS)
              .setPauseRequest(1500L, TimeUnit.MILLISECONDS)
              .setMaxUnitWorkingTime(ModelConstants.FIVE_MINUTES)
              .setMeaningfulRestartTime(ModelConstants.TEN_MINUTES)
              .setSource(Source.atlas)
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
            availableCities.put(id, cityNode.get("name").asText());

            addTask(new Link("%s/search/suggest?user_input=&from_id=%s&to_id=&locale=ru".formatted(API_ORIGIN, id)))
                  .success(endpointsSe -> handleAvailableEndpoints(endpointsSe, id));
        }
    }

    private void handleAvailableEndpoints(HttpResponse availableEndpointsSe, String fromId) {
        for (JsonNode endpointNode : availableEndpointsSe.json()) {
            String id = endpointNode.get("id").asText();
            availableCities.computeIfAbsent(id, s -> endpointNode.get("name").asText());

            for (int i = 0; i < COLLECTED_DAYS_COUNT; i++) {
                LinkBuilder link = new LinkBuilder("%s/search".formatted(API_ORIGIN))
                      .addParam("calendar_width", "30")
                      .addParam("date", LocalDate.now().plusDays(i).format(DateTimeFormatter.ISO_DATE))
                      .addParam("passengers", "1");

                addTask(link.addParam("from_id", fromId).addParam("to_id", id).build()).success(this::handleTask);
                addTask(link.addParam("from_id", id).addParam("to_id", fromId).build()).success(this::handleTask);
            }
        }
    }

    private void handleTask(HttpResponse successEvent) {
        for (JsonNode rideNode : successEvent.json().at("/rides")) {
            String fromName = rideNode.at("/from").get("desc").textValue();
            String fromId = rideNode.at("/from").get("id").textValue();
            String toName = rideNode.at("/to").get("desc").textValue();
            String toId = rideNode.at("/to").get("id").textValue();
            String time = rideNode.get("departure").textValue();
            String freeSeats = rideNode.get("freeSeats").asText();

            String rideId = rideNode.get("id").textValue();
            if (rideId.isBlank()) {
                continue;
            }
            putTask(new Task(rideId)
                  .startCity(fromName, fromId)
                  .endCity(toName, toId)
                  .date(time)
                  .availablePlaces(freeSeats)
                  .link(generateRideLink(rideId, fromId, toId, time))
            );
        }
    }

    public String generateRideLink(String rideId, String fromCity, String toCity, String date) {
        return "%s/booking/%s?passengers=1&from=%s&to=%s&date=%s&pickup=&discharge="
              .formatted(ORIGIN, rideId, fromCity, toCity, date);
    }

}
