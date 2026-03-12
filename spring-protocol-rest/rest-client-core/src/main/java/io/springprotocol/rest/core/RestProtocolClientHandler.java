package io.springprotocol.rest.core;

import io.springprotocol.core.annotation.ProtocolType;
import io.springprotocol.core.spi.ClientDefinition;
import io.springprotocol.core.spi.ProtocolClientHandler;

import java.lang.reflect.Proxy;
import java.net.http.HttpClient;

/**
 * REST implementation of {@link ProtocolClientHandler}.
 * Creates JDK dynamic proxies backed by {@link RestClientProxy}.
 */
public class RestProtocolClientHandler implements ProtocolClientHandler {

    private final HttpClient httpClient;

    public RestProtocolClientHandler() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public RestProtocolClientHandler(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public ProtocolType protocol() {
        return ProtocolType.REST;
    }

    @Override
    public Object createProxy(ClientDefinition definition) {
        String baseUrl = (String) definition.attributes().get("address");
        if (baseUrl == null) {
            baseUrl = "";
        }

        var handler = new RestClientProxy(baseUrl, httpClient);
        return Proxy.newProxyInstance(
                definition.interfaceType().getClassLoader(),
                new Class<?>[]{definition.interfaceType()},
                handler
        );
    }

    @Override
    public void destroy() {
        // no-op: HttpClient manages its own lifecycle
    }
}
