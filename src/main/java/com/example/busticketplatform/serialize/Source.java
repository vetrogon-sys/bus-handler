package com.example.busticketplatform.serialize;

import com.example.busticketplatform.scunners.SiteCrawler;
import com.example.busticketplatform.scunners.crawler.AtlasSiteCrawler;
import com.example.busticketplatform.scunners.crawler.GeoNodeSiteCrawler;
import lombok.Getter;

public enum Source {
    atlas(1, "Atlas", AtlasSiteCrawler.ORIGIN, AtlasSiteCrawler.class),

    /*PROXY-CRAWLERS*/
    geonode(2, "GeoNode (PROXY)", GeoNodeSiteCrawler.ORIGIN, GeoNodeSiteCrawler.class);

    private final int id;
    private final String viewName;
    private final String origin;

    @Getter
    private final Class<? extends SiteCrawler> crawler;

    Source(int id, String viewName, String origin, Class<? extends SiteCrawler> crawler) {
        this.viewName = viewName;
        this.origin = origin;
        this.id = id;
        this.crawler = crawler;
    }

}
