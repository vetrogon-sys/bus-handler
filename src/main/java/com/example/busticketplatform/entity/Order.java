package com.example.busticketplatform.entity;

import com.example.busticketplatform.dto.Filter;
import com.example.busticketplatform.serialize.BusSource;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Order {

    private Long id;
    private BusSource source;
    private Filter rideFilter;
    private Long postingDate;
    private Long customerId;

}
