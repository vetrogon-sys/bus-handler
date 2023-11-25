package com.example.busticketplatform.utl;

import com.example.busticketplatform.scunners.model.config.ModelConstants;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

import static com.example.busticketplatform.scunners.model.config.ModelConstants.DEFAULT_TIME_ZONE;

public final class EpochUtil {

    private static final Pattern EPOCH_NUMBER_PATTERN = Pattern.compile("\\d+");

    public static Long getEpochFromDate(String date) {
        return getEpochFromDate(date, DateTimeFormatter.ISO_DATE_TIME);
    }

    public static Long getEpochFromDate(String date, String datePattern) {
        return getEpochFromDate(date, DateTimeFormatter.ofPattern(datePattern));
    }

    private static Long getEpochFromDate(String date, DateTimeFormatter datePattern) {
        try {
            return getEpochFromLocalDateTime(LocalDateTime.parse(date, datePattern));
        } catch (DateTimeParseException e) {
            return getDefaultEpoch();
        }
    }

    public static Long getEpochFromStringEpoch(String epochDate) {
        return EPOCH_NUMBER_PATTERN.matcher(epochDate).matches()
              ? Long.parseLong(epochDate)
              : getEpochFromLocalDateTime(LocalDateTime.MIN);
    }


    public static LocalDateTime getDateFromEpoch(String epoch) {
        return getDateFromEpoch(getEpochFromStringEpoch(epoch));
    }

    public static LocalDateTime getDateFromEpoch(Long epoch) {
        return Instant.ofEpochMilli(epoch)
              .atZone(ZoneId.of(ModelConstants.DEFAULT_TIME_ZONE)).toLocalDateTime();
    }

    public static String getFormattedDateFromEpoch(String epoch) {
        return getDateFromEpoch(epoch).format(DateTimeFormatter.ISO_DATE_TIME);
    }

    public static String getFormattedDateFromEpoch(Long epoch) {
        return getDateFromEpoch(epoch).format(DateTimeFormatter.ISO_DATE_TIME);
    }

    private static long getEpochFromLocalDateTime(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.of(DEFAULT_TIME_ZONE)).toInstant().toEpochMilli();
    }

    private static Long getDefaultEpoch() {
        return LocalDateTime.MIN.atZone(ZoneId.of(DEFAULT_TIME_ZONE)).toInstant().toEpochMilli();
    }

}
