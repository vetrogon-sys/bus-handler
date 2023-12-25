package com.example.busticketplatform.web.services;

import com.example.busticketplatform.web.services.proxy.ProxyInstance;
import lombok.RequiredArgsConstructor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;

@Service
@RequiredArgsConstructor
public class RestTemplateServiceImpl implements RestTemplateService {

    @Override
    public RestTemplate buildRestTemplate(ProxyInstance proxyInstance) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        Proxy proxy;
        if (proxyInstance == null) {
            proxy = Proxy.NO_PROXY;
        } else {
            proxy = new Proxy(proxyInstance.getType(), new InetSocketAddress(proxyInstance.getIp(), proxyInstance.getPort()));
        }
        requestFactory.setProxy(proxy);
        return new RestTemplate(requestFactory);
    }
}
