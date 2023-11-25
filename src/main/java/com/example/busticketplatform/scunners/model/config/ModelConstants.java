package com.example.busticketplatform.scunners.model.config;

import java.util.concurrent.TimeUnit;

public interface ModelConstants {

    long ONE_MINUTE = TimeUnit.MINUTES.toMillis(1);
    long FIVE_MINUTES = TimeUnit.MINUTES.toMillis(5);
    long TEN_MINUTES = TimeUnit.MINUTES.toMillis(10);

    String ID = "id";
    String START_CITY = "startCity";
    String START_CITY_ID = "startCityId";
    String END_CITY = "endCity";
    String END_CITY_ID = "endCityId";
    String DATE = "date";
    String AVAILABLE_PLACES = "availablePlaces";
    String RIDE_LINK = "rideLink";
    String PROXY_IP = "proxyIp";
    String PROXY_PORT = "proxyPort";
    String PROXY_COUNTRY = "proxyCountry";
    String PROXY_PROTOCOL = "proxyProtocol";
    String DEFAULT_TIME_ZONE = "Europe/London";

}
