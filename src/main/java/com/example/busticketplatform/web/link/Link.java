package com.example.busticketplatform.web.link;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpMethod;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Link {

    private String url;

    private HttpMethod method;

    private @Nullable String body;

    @Override
    public String toString() {
        return method + "+'" + url + "'" + (body != null ? "-data-row: " + body : "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Link link)) {
            return false;
        }

        if (!Objects.equals(url, link.url)) {
            return false;
        }
        if (!Objects.equals(method, link.method)) {
            return false;
        }
        return Objects.equals(body, link.body);
    }

    @Override
    public int hashCode() {
        int result = url != null ? url.hashCode() : 0;
        result = 31 * result + (method != null ? method.hashCode() : 0);
        result = 31 * result + (body != null ? body.hashCode() : 0);
        return result;
    }
}
