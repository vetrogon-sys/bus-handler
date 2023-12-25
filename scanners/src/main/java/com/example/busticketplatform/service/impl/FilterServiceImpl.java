package com.example.busticketplatform.service.impl;

import com.example.busticketplatform.dto.Filter;
import com.example.busticketplatform.dto.FilterToken;
import com.example.busticketplatform.service.FilterService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class FilterServiceImpl implements FilterService {

    final Map<Long, Filter> filtersMap = new ConcurrentHashMap<>();

    final Pattern pattern = Pattern.compile("(%s\\s*(?:[a-zA-Z0-9а-яА-Я]+-?.?\\s?)+\\s*)".formatted(FilterToken.getTokensAsRegex()));

    @Override
    public Filter addFilterParam(long chatId, String message) {
        Matcher matcher = pattern.matcher(message);
        List<String> tokensString = new ArrayList<>();
        while (matcher.find()) {
            tokensString.add(matcher.group(1));
        }
        Filter filter = filtersMap.computeIfAbsent(chatId, i -> new Filter());
        if (CollectionUtils.isEmpty(tokensString)) {
            return filter;
        }
        for (String tokenString : tokensString) {
            FilterToken token = FilterToken.findTokenTypeInMessage(tokenString);
            if (token == null) {
                continue;
            }
            String filterMessage = StringUtils.substringAfter(tokenString, token.token).trim();
            filter.setFiled(token, filterMessage);
        }

        return filter;
    }

    @Override
    public Filter getFilter(long chatId) {
        return filtersMap.remove(chatId);
    }

    @Override
    public boolean isStartWorkWithFilter(long chatId) {
        return filtersMap.containsKey(chatId);
    }

    @Override
    public void initFilter(long chatId) {
        filtersMap.put(chatId, new Filter());
    }
}
