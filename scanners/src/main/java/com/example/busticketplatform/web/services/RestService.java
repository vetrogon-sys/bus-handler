package com.example.busticketplatform.web.services;

import com.example.busticketplatform.web.HttpResponse;
import com.example.busticketplatform.web.link.Link;
import lombok.Setter;
import org.slf4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import static org.slf4j.LoggerFactory.getLogger;

public class RestService {
    private static final Logger log = getLogger(RestService.class);

    @Setter
    RestTemplate restTemplate;

    public HttpResponse execute(Link link) {
        ResponseEntity<String> response;
        try {
            HttpEntity<String> entity = new HttpEntity<>(link.getBody(), link.getHeaders());
            log.info("Execute {}", link);
            response = restTemplate.exchange(link.getUrl(), link.getMethod(), entity, String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                log.debug("Error via {}, Status {}, Error {}", link, response.getStatusCode(), response.getBody());
            }
        } catch (HttpStatusCodeException e) {
            return new HttpResponse(e.getStatusCode(), e.getMessage());
        } catch (ResourceAccessException e) {
            log.warn(e.getMessage());
            return HttpResponse.empty();
        }

        return new HttpResponse(response.getStatusCode(), response.getBody());
    }

}
