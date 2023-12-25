package com.example.busticketplatform.web.services;

import com.example.busticketplatform.web.services.proxy.ProxyInstance;
import org.springframework.web.client.RestTemplate;

public interface RestTemplateService {

    RestTemplate buildRestTemplate(ProxyInstance proxy);

}
