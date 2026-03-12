package io.springprotocol.graphql.core;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.springprotocol.core.annotation.ProtocolType;
import io.springprotocol.core.spi.ClientDefinition;
import io.springprotocol.core.spi.ProtocolClientHandler;

import java.lang.reflect.Proxy;
import java.net.http.HttpClient;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class GraphqlProtocolClientHandler implements ProtocolClientHandler {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final ConcurrentMap<Class<?>, Object> proxyCache = new ConcurrentHashMap<>();

    public GraphqlProtocolClientHandler() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = createObjectMapper();
    }

    public GraphqlProtocolClientHandler(HttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public ProtocolType protocol() {
        return ProtocolType.GRAPHQL;
    }

    @Override
    public Object createProxy(ClientDefinition definition) {
        return proxyCache.computeIfAbsent(definition.interfaceType(), type -> {
            String endpoint = (String) definition.attributes().get("address");
            if (endpoint == null) {
                endpoint = "";
            }

            var handler = new GraphqlClientProxy(endpoint, httpClient, objectMapper);
            return Proxy.newProxyInstance(
                    type.getClassLoader(),
                    new Class<?>[]{type},
                    handler
            );
        });
    }

    @Override
    public void destroy() {
        proxyCache.clear();
    }

    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }
}
