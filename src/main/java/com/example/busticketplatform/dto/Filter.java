package com.example.busticketplatform.dto;

import com.example.busticketplatform.scunners.model.entity.Task;
import com.example.busticketplatform.utl.EpochUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Filter {

    private String from;
    private String to;
    private String date;
    private int requiredPlaces;

    public boolean test(Task task) {
        boolean isTaskValid = true;

        if (StringUtils.isNotBlank(from)) {
            isTaskValid = from.equals(task.getStartCity());
        }
        if (StringUtils.isNoneBlank(to)) {
            isTaskValid = isTaskValid && to.equals(task.getEndCity());
        }
        if (requiredPlaces != 0) {
            isTaskValid = isTaskValid && requiredPlaces <= Integer.parseInt(task.getAvailablePlaces());
        }

        if (StringUtils.isNoneBlank(date)) {
            LocalDateTime filterDate = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
            LocalDateTime taskDate = EpochUtil.getDateFromEpoch(task.getDate());

            isTaskValid = isTaskValid && (filterDate.isBefore(taskDate) || filterDate.isEqual(taskDate));
        }

        return isTaskValid;
    }

    public Filter setFiled(FilterToken token, String message) {
        switch (token) {
            case DATE -> date = message;
            case AVAILABLE_PLACES -> requiredPlaces = Integer.parseInt(message);
            case CITY_FROM -> from = message;
            case CITY_TO -> to = message;
        }
        return this;
    }

    @Override
    public String toString() {
        return "Filter {" + '\n' +
              "from='" + from + '\'' + '\n' +
              ", to='" + to + '\'' + '\n' +
              ", date='" + date + '\'' + '\n' +
              ", requiredPlaces=" + requiredPlaces + '\n' +
              '}';
    }
}
