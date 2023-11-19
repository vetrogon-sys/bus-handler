package com.example.busticketplatform.web.services.proxy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.Proxy;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProxyInstance {

    String id;
    int port;
    String ip;
    String country;
    Proxy.Type type;

    @Override
    public String toString() {
        return "%s:%d [%s] --%s".formatted(ip, port, country, type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProxyInstance that)) {
            return false;
        }

        if (port != that.port) {
            return false;
        }
        if (!Objects.equals(id, that.id)) {
            return false;
        }
        if (!Objects.equals(ip, that.ip)) {
            return false;
        }
        return Objects.equals(country, that.country);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + port;
        result = 31 * result + (ip != null ? ip.hashCode() : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        return result;
    }
}
