package com.example.busticketplatform.serialize;

import com.example.busticketplatform.scunners.model.SiteCrawler;
import com.example.busticketplatform.scunners.crawler.atlas.AtlasSiteCrawler;
import com.example.busticketplatform.scunners.crawler.geonode.GeoNodeSiteCrawler;
import lombok.Getter;

public enum BusSource implements Source {
    atlas(1, "Atlas", AtlasSiteCrawler.ORIGIN, AtlasSiteCrawler.class);

    private final int id;
    private final String viewName;
    private final String origin;

    @Getter
    private final Class<? extends SiteCrawler> crawler;

    BusSource(int id, String viewName, String origin, Class<? extends SiteCrawler> crawler) {
        this.viewName = viewName;
        this.origin = origin;
        this.id = id;
        this.crawler = crawler;
    }

}
