package com.example.busticketplatform.service;

import com.example.busticketplatform.dto.Filter;
import com.example.busticketplatform.entity.Ride;
import com.example.busticketplatform.serialize.Source;

import java.util.List;

public interface RideService {

    List<Ride> getAllRides(Source source);

    List<Ride> getRides(Source source, Filter filter);

}
