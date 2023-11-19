package com.example.busticketplatform.web.link;

import jakarta.annotation.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class LinkBuilder {

    final String uri;
    HttpMethod method = HttpMethod.GET;
    List<Parameter> params;

    @Nullable String body;

    Map<String, List<String>> headers = new HashMap<>();

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

    public LinkBuilder addHeader(String key, String value) {
        headers.computeIfAbsent(key, s -> new ArrayList<>())
              .add(value);
        return this;
    }

    public LinkBuilder putHeader(String key, String value) {
        headers.put(key, new ArrayList<>(List.of(value)));
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
        HttpHeaders httpHeaders = new HttpHeaders();
        if (!headers.isEmpty()) {
            headers.forEach(httpHeaders::addAll);
        }
        return new Link(url.toString(), method, body, httpHeaders);
    }

}
