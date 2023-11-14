package com.example.busticketplatform.web.link;

import jakarta.annotation.Nullable;
import org.springframework.http.HttpMethod;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class LinkBuilder {

    final String uri;
    HttpMethod method = HttpMethod.GET;
    List<Parameter> params;

    @Nullable String body;

    public LinkBuilder(String uri) {
        this.uri = uri;
    }

    public static LinkBuilder formatted(String uri, String... params) {
        return new LinkBuilder(uri.formatted((Object[]) params));
    }

    public LinkBuilder method(HttpMethod method) {
        this.method = method;
        return this;
    }

    public LinkBuilder addParam(String name, String value) {
        return addParam(new Parameter(name, value));
    }

    public LinkBuilder addParam(Parameter param) {
        if (CollectionUtils.isEmpty(params)) {
            params = new ArrayList<>();
        }
        params.add(param);
        return this;
    }

    public LinkBuilder addBody(String body) {
        this.body = body;
        return this;
    }

    public Link build() {
        StringBuilder url = new StringBuilder(uri);
        if (!CollectionUtils.isEmpty(params)) {
            url.append("?");
            IntStream.range(0, params.size()).forEachOrdered(i -> {
                url.append(params.get(i));
                if (i != params.size() - 1) {
                    url.append("&");
                }
            });
        }
        return new Link(url.toString(), method, body);
    }

}
