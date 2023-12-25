package com.example.busticketplatform.controller;

import com.example.busticketplatform.dto.Filter;
import com.example.busticketplatform.entity.Ride;
import com.example.busticketplatform.serialize.BusSource;
import com.example.busticketplatform.service.RideService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rides")
@RequiredArgsConstructor
public class RideController {

    private final RideService rideService;

    @GetMapping("/{source}")
    public ResponseEntity<List<Ride>> getAll(@PathVariable String source) {
        BusSource sourceEnum = BusSource.valueOf(source);
        return ResponseEntity.ok(rideService.getAllRides(sourceEnum));
    }

    @PostMapping("/{source}")
    public ResponseEntity<List<Ride>> getAll(@PathVariable String source, @RequestBody Filter filter) {
        BusSource sourceEnum = BusSource.valueOf(source);
        return ResponseEntity.ok(rideService.getRides(sourceEnum, filter));
    }

}
