package com.example.busticketplatform.service;

import com.example.busticketplatform.dto.Filter;
import com.example.busticketplatform.entity.Ride;
import com.example.busticketplatform.serialize.BusSource;

import java.util.List;

public interface RideService {

    List<Ride> getAllRides(BusSource source);

    List<Ride> getRides(BusSource source, Filter filter);

}
