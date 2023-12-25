package com.example.busticketplatform.web.services.proxy;

import com.example.busticketplatform.web.HttpResponse;
import com.example.busticketplatform.web.link.Link;
import com.example.busticketplatform.web.link.LinkBuilder;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Predicate;

@Getter
public class CheckProxySettings {

    Link checkProxyLink;
    Predicate<HttpResponse> checkProxyPredicate;

    public CheckProxySettings(String findString) {
        this.checkProxyPredicate = (httpResponse) -> StringUtils.containsIgnoreCase(httpResponse.string(), findString);
    }

    public CheckProxySettings link(String link) {
        this.checkProxyLink = new LinkBuilder(link).build();
        return this;
    }

    public CheckProxySettings link(Link link) {
        this.checkProxyLink = link;
        return this;
    }

}
