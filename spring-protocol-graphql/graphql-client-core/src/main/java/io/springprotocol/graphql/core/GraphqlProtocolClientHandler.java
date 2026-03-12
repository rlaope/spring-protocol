package io.springprotocol.graphql.core;

import io.springprotocol.core.annotation.ProtocolType;
import io.springprotocol.core.spi.ClientDefinition;
import io.springprotocol.core.spi.ProtocolClientHandler;

import java.lang.reflect.Proxy;
import java.net.http.HttpClient;

/**
 * GraphQL implementation of {@link ProtocolClientHandler}.
 * Creates JDK dynamic proxies backed by {@link GraphqlClientProxy}.
 */
public class GraphqlProtocolClientHandler implements ProtocolClientHandler {

    private final HttpClient httpClient;

    public GraphqlProtocolClientHandler() {
        this.httpClient = HttpClient.newHttpClient();
    }

    @Override
    public ProtocolType protocol() {
        return ProtocolType.GRAPHQL;
    }

    @Override
    public Object createProxy(ClientDefinition definition) {
        String endpoint = (String) definition.attributes().get("address");
        if (endpoint == null) {
            endpoint = "";
        }

        var handler = new GraphqlClientProxy(endpoint, httpClient);
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
