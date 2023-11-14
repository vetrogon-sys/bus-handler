package com.example.busticketplatform.serialize;

import com.example.busticketplatform.scunners.crawler.AtlasSiteCrawler;

public enum Source {
    atlas(1, "Atlas", AtlasSiteCrawler.ORIGIN);

    private final int id;
    private final String name;
    private final String origin;

    Source(int id, String name, String origin) {
        this.name = name;
        this.origin = origin;
        this.id = id;
    }
}
