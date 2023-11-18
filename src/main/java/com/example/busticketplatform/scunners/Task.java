package com.example.busticketplatform.scunners;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static com.example.busticketplatform.scunners.ModelConstants.*;

@Data
public class Task implements Serializable {

    private String id;
    private String date;
    private String endCity;
    private String endCityId;
    private String startCity;
    private String startCityId;
    private String link;
    private String availablePlaces;

    private Map<String, String> params;

    public Task(String id) {
        params = new ConcurrentHashMap<>();
        params.put(ID, id);
        this.id = id;
    }

    public Task availablePlaces(String availablePlaces) {
        this.availablePlaces = availablePlaces;
        return param(AVAILABLE_PLACES, availablePlaces);
    }

    public Task date(Long date) {
        this.date = String.valueOf(date);
        return param(DATE, this.date);
    }

    public Task date(String date) {
        return date(date, "yyyy-MM-dd'T'HH:mm:ss");
    }

    public Task date(String date, String pattern) {
        this.date = String.valueOf(LocalDateTime.parse(date, DateTimeFormatter.ofPattern(pattern)).atZone(ZoneId.of(DEFAULT_TIME_ZONE)).toInstant().toEpochMilli());
        return param(DATE, this.date);
    }

    public Task endCity(String city, String id) {
        this.endCity = city;
        this.endCityId = id;
        return param(END_CITY, city).
              param(END_CITY_ID, id);
    }

    public Task endCity(String city) {
        this.endCity = city;
        return param(END_CITY, city);
    }

    public Task startCity(String city) {
        this.startCity = city;
        return param(START_CITY, city);
    }

    public Task startCity(String city, String id) {
        this.startCity = city;
        this.startCityId = id;
        return param(START_CITY, city)
              .param(START_CITY_ID, id);
    }

    public Task link(String link) {
        this.link = link;
        return param(RIDE_LINK, link);
    }

    public Task param(String key, String value) {
        params.put(key, value);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Task task)) {
            return false;
        }

        if (!Objects.equals(id, task.id)) {
            return false;
        }
        if (!Objects.equals(date, task.date)) {
            return false;
        }
        if (!Objects.equals(endCity, task.endCity)) {
            return false;
        }
        if (!Objects.equals(endCityId, task.endCityId)) {
            return false;
        }
        if (!Objects.equals(startCity, task.startCity)) {
            return false;
        }
        if (!Objects.equals(startCityId, task.startCityId)) {
            return false;
        }
        return Objects.equals(link, task.link);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (endCity != null ? endCity.hashCode() : 0);
        result = 31 * result + (endCityId != null ? endCityId.hashCode() : 0);
        result = 31 * result + (startCity != null ? startCity.hashCode() : 0);
        result = 31 * result + (startCityId != null ? startCityId.hashCode() : 0);
        result = 31 * result + (link != null ? link.hashCode() : 0);
        return result;
    }
}
