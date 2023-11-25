package com.example.busticketplatform.serialize;

import com.example.busticketplatform.scunners.model.SiteCrawler;

public interface Source {

    String name();
    Class<? extends SiteCrawler> getCrawler();

}
