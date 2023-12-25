package com.example.busticketplatform.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Builder
public class Ride {

    private String id;
    private String link;
    private String from;
    private String to;
    private String time;
    private String availablePlaces;

    public String getFormattedDate() {
        LocalDateTime parse = LocalDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME);
        return parse.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }

    public String toShortString() {
        return """
              <b>%s-%s</b> %s
              places: %s
              <a href="%s">go ot site</a>
              """.formatted(from, to, getFormattedDate(), availablePlaces, link);
    }
    @Override
    public String toString() {
        return "Ride{" + '\n' +
              "id='" + id + '\'' + '\n' +
              ", from='" + from + '\'' + '\n' +
              ", to='" + to + '\'' + '\n' +
              ", time='" + time + '\'' + '\n' +
              ", availablePlaces='" + availablePlaces + '\'' + '\n' +
              ", link='" + link + '\'' + '\n' +
              '}';
    }
}
