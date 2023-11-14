package com.example.busticketplatform.web.services;

import com.example.busticketplatform.web.HttpResponse;
import com.example.busticketplatform.web.link.Link;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.slf4j.LoggerFactory.getLogger;

@Component
@RequiredArgsConstructor
public class RestService {
    private static final Logger log = getLogger(RestService.class);

    final RestTemplate restTemplate;

    public HttpResponse execute(Link link) {
        log.info("Execute {}", link.toString());
        ResponseEntity<String> response;
        try {
            response = restTemplate.execute(link.getUrl(), link.getMethod(), null, restTemplate.responseEntityExtractor(String.class));
            if (response != null && !response.getStatusCode().is2xxSuccessful()) {
                log.debug("Error via {}, Status {}, Error {}", link, response.getStatusCode(), response.getBody());
            }
        } catch (HttpStatusCodeException e) {
            return new HttpResponse(e.getStatusCode(), e.getMessage());
        }

        if (response == null) {
            return HttpResponse.empty();
        }
        return new HttpResponse(response.getStatusCode(), response.getBody());
    }

}
