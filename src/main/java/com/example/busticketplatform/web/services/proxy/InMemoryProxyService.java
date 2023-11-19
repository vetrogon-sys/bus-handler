package com.example.busticketplatform.web.services.proxy;

import com.example.busticketplatform.scunners.ModelConstants;
import com.example.busticketplatform.scunners.Task;
import com.example.busticketplatform.serialize.Source;
import com.example.busticketplatform.serialize.TaskSerializer;
import com.example.busticketplatform.web.HttpResponse;
import com.example.busticketplatform.web.link.Link;
import com.example.busticketplatform.web.services.RestService;
import com.example.busticketplatform.web.services.RestTemplateService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.slf4j.LoggerFactory.getLogger;

@Service
@RequiredArgsConstructor
public class InMemoryProxyService implements ProxyService {
    private static final Logger log = getLogger(InMemoryProxyService.class);

    final TaskSerializer taskSerializer;
    final RestTemplateService restTemplateService;

    Map<Link, List<String>> restrictedProxies = new ConcurrentHashMap<>();

    @Override
    public RestService buildTemplateWithProxy(CheckProxySettings checkProxySettings) {

        final RestService restService = new RestService();
        RestTemplate restTemplate = buildTemplateWithProxy(checkProxySettings, restService);
        if (restTemplate == null) {
            return null;
        }

        restService.setRestTemplate(restTemplate);
        return restService;
    }

    @Override
    public RestService buildTemplateWithoutProxy() {
        final RestService restService = new RestService();
        restService.setRestTemplate(getWithoutProxyTemplate());
        return restService;
    }

    private RestTemplate buildTemplateWithProxy(CheckProxySettings checkProxySettings, RestService restService) {
        Map<String, Task> proxyTasks = taskSerializer.readTasks(Source.geonode);
        for (Map.Entry<String, Task> entry : proxyTasks.entrySet()) {
            String id = entry.getKey();
            Task proxyTask = entry.getValue();
            ProxyInstance proxyInstance = getProxyInstanceFromTask(id, proxyTask);
            Link checkProxyLink = checkProxySettings.getCheckProxyLink();
            if (restrictedProxies.get(checkProxyLink) != null) {
                if (restrictedProxies.get(checkProxyLink).contains(proxyInstance.country)) {
                    continue;
                }
            }

            RestTemplate restTemplate = restTemplateService.buildRestTemplate(proxyInstance);
            if (restTemplate != null) {
                restService.setRestTemplate(restTemplate);

                log.warn("Check proxy {}", proxyInstance);
                HttpResponse checkProxyResponse = restService.execute(checkProxyLink);
                boolean test = checkProxySettings.getCheckProxyPredicate()
                      .test(checkProxyResponse);

                if (test) {
                    log.info("Find proxy {}", proxyInstance);
                    return restTemplate;
                } else {
                    restrictedProxies.computeIfAbsent(checkProxyLink, s -> new ArrayList<>())
                          .add(proxyInstance.country);
                    restService.setRestTemplate(null);
                    log.info("Restricted proxy {}, {}", proxyTask, checkProxyResponse.string());
                }
            }
        }
        return null;
    }

    private RestTemplate getWithoutProxyTemplate() {
        return restTemplateService.buildRestTemplate(null);
    }

    private ProxyInstance getProxyInstanceFromTask(String id, Task proxyTask) {
        String proxyIp = proxyTask.getParam(ModelConstants.PROXY_IP);
        String proxyProtocol = proxyTask.getParam(ModelConstants.PROXY_PROTOCOL);
        String proxyPort = proxyTask.getParam(ModelConstants.PROXY_PORT);
        String proxyCountry = proxyTask.getParam(ModelConstants.PROXY_COUNTRY);
        return new ProxyInstance(id, Integer.parseInt(proxyPort), proxyIp, proxyCountry, getProxyType(proxyProtocol));
    }

    private Proxy.Type getProxyType(String protocol) {
        return switch (protocol) {
            case "socks4", "socks5" -> Proxy.Type.SOCKS;
            case "http", "https" -> Proxy.Type.HTTP;
            default -> Proxy.Type.DIRECT;
        };
    }
}
