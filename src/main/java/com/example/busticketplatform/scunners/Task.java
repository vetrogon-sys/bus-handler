package com.example.busticketplatform.scunners;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static com.example.busticketplatform.scunners.TaskFieldNames.*;

@Data
@Builder
public class Task implements Serializable {

    private String id;

    private Map<String, String> params;

    public Task(String id) {
        params = new ConcurrentHashMap<>();
        params.put(ID, id);
        this.id = id;
    }

    public Task availablePlaces(String availablePlaces) {
        return param(AVAILABLE_PLACES, availablePlaces);
    }

    public Task date(Long date) {
        return param(DATE, String.valueOf(date));
    }

    public Task date(String date) {
        return date(date, DateTimeFormatter.ISO_TIME.toString());
    }

    public Task date(String date, String pattern) {
        return param(DATE, String.valueOf(ZonedDateTime.parse(date, DateTimeFormatter.ofPattern(pattern)).toInstant().toEpochMilli()));
    }

    public Task endCity(String city, String id) {
        return param(END_CITY, city).
              param(END_CITY_ID, id);
    }

    public Task endCity(String city) {
        return param(END_CITY, city);
    }

    public Task startCity(String city) {
        return param(START_CITY, city);
    }

    public Task startCity(String city, String id) {
        return param(START_CITY, city)
              .param(START_CITY_ID, id);
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
        return Objects.equals(params, task.params);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (params != null ? params.hashCode() : 0);
        return result;
    }
}
