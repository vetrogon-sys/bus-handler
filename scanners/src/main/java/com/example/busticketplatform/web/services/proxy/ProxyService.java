package com.example.busticketplatform.web.services.proxy;

import com.example.busticketplatform.web.services.RestService;

public interface ProxyService {

    RestService buildTemplateWithProxy(CheckProxySettings checkProxySettings);

    RestService buildTemplateWithoutProxy();

}
