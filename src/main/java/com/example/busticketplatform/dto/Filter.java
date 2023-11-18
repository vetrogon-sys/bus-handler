package com.example.busticketplatform.dto;

import com.example.busticketplatform.scunners.ModelConstants;
import com.example.busticketplatform.scunners.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Filter {

    private String from;
    private String to;
    private String date;

    public boolean test(Task task) {
        boolean isTaskValid = true;

        if (StringUtils.isNotBlank(from)) {
            isTaskValid = from.equals(task.getStartCity());
        }
        if (StringUtils.isNoneBlank(to)) {
            isTaskValid = isTaskValid && to.equals(task.getEndCity());
        }

        if (StringUtils.isNoneBlank(date)) {
            LocalDateTime parsedDate = LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME);
            LocalDateTime taskDate = Instant.ofEpochMilli(Long.parseLong(task.getDate()))
                  .atZone(ZoneId.of(ModelConstants.DEFAULT_TIME_ZONE)).toLocalDateTime();

            isTaskValid = isTaskValid && parsedDate.isBefore(taskDate);
        }

        return isTaskValid;
    }

}
