package com.example.busticketplatform.service;

import com.example.busticketplatform.dto.Filter;

public interface FilterService {

    Filter addFilterParam(long chatId, String message);

    Filter getFilter(long chatId);

    boolean isStartWorkWithFilter(long chatId);

    void initFilter(long chatId);

}
