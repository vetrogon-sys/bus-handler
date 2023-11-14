package com.example.busticketplatform.scunners;

import com.example.busticketplatform.web.HttpResponse;
import com.example.busticketplatform.web.link.Link;
import lombok.Builder;
import lombok.Getter;

import java.util.Objects;
import java.util.function.Consumer;

@Getter
@Builder
public class CrawlerTask<T> {

    private final Link link;

    private Consumer<HttpResponse> postProcess;

    public CrawlerTask(Link link, Consumer<HttpResponse> postProcess) {
        this.link = link;
        this.postProcess = postProcess;
    }

    public String getUrl() {
        return link.getUrl();
    }

    public void success(Consumer<HttpResponse> postProcess) {
        this.postProcess = postProcess;
    }

    @Override
    public String toString() {
        return "CrawlerTask{" +
              "link=" + link +
              '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CrawlerTask<?> that)) {
            return false;
        }

        return Objects.equals(link, that.link);
    }

    @Override
    public int hashCode() {
        return link != null ? link.hashCode() : 0;
    }

}
