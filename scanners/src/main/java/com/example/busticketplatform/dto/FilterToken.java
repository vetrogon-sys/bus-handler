package com.example.busticketplatform.dto;

import org.apache.commons.lang3.StringUtils;

public enum FilterToken {

    CITY_FROM("-fc", "name of city-from"),
    CITY_TO("-tc", "name of city-to"),
    DATE("-dt", "ride date"),
    AVAILABLE_PLACES("-p", "required places in a bus");

    public final String token;
    public final String description;

    FilterToken(String token, String description) {
        this.token = token;
        this.description = description;
    }

    public static FilterToken findTokenTypeInMessage(String message) {
        FilterToken[] values = values();
        for (FilterToken token : values) {
            if (StringUtils.containsIgnoreCase(message, token.token)) {
                return token;
            }
        }
        return null;
    }

    public static String getTokensAsRegex() {
        StringBuilder regex = new StringBuilder("(?:");
        FilterToken[] values = values();
        for (int i = 0; i < values.length; i++) {
            regex.append(values[i].token);
            if (i != values.length - 1) {
                regex.append("|");
            }
        }
        regex.append(")");
        return regex.toString();
    }
}
