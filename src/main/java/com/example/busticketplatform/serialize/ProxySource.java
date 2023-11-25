package com.example.busticketplatform.serialize;

import com.example.busticketplatform.scunners.crawler.geonode.GeoNodeSiteCrawler;
import com.example.busticketplatform.scunners.model.SiteCrawler;
import lombok.Getter;

public enum ProxySource implements Source {

    geonode("GeoNode (PROXY)", GeoNodeSiteCrawler.class);

    private final String viewName;

    @Getter
    private final Class<? extends SiteCrawler> crawler;

    ProxySource(String viewName, Class<? extends SiteCrawler> crawler) {
        this.viewName = viewName;
        this.crawler = crawler;
    }

}
