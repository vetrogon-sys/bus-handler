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

}
