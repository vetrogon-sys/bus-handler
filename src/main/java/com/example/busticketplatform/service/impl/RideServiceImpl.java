package com.example.busticketplatform.service.impl;

import com.example.busticketplatform.dto.Filter;
import com.example.busticketplatform.entity.Ride;
import com.example.busticketplatform.scunners.Task;
import com.example.busticketplatform.serialize.Source;
import com.example.busticketplatform.serialize.TaskSerializer;
import com.example.busticketplatform.service.RideService;
import com.example.busticketplatform.utl.EpochUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RideServiceImpl implements RideService {

    private final TaskSerializer taskSerializer;

    @Override
    public List<Ride> getAllRides(Source source) {

        return taskSerializer.readTasks(source).values().stream()
              .map(this::getRideFromTask)
              .toList();
    }

    @Override
    public List<Ride> getRides(Source source, Filter filter) {
        return taskSerializer.readTasks(source).values().stream()
              .filter(filter::test)
              .map(this::getRideFromTask)
              .toList();
    }

    private Ride getRideFromTask(Task task) {
        return Ride.builder()
              .id(task.getId())
              .from(task.getStartCity())
              .to(task.getEndCity())
              .time(EpochUtil.getFormattedDateFromEpoch(task.getDate()))
              .availablePlaces(task.getAvailablePlaces())
              .link(task.getLink())
              .build();
    }
}
