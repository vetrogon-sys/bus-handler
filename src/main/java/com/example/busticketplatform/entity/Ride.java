package com.example.busticketplatform.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Ride {

    private String id;
    private String link;
    private String from;
    private String to;
    private String time;
    private String availablePlaces;

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
