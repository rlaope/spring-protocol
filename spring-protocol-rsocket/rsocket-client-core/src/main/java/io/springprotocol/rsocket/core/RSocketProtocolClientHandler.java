package io.springprotocol.rsocket.core;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.rsocket.RSocket;
import io.rsocket.core.RSocketConnector;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.springprotocol.core.annotation.ProtocolType;
import io.springprotocol.core.spi.ClientDefinition;
import io.springprotocol.core.spi.ProtocolClientHandler;

import java.lang.reflect.Proxy;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RSocketProtocolClientHandler implements ProtocolClientHandler {

    private final ObjectMapper objectMapper;
    private final ConcurrentMap<String, RSocket> connectionCache = new ConcurrentHashMap<>();
    private final ConcurrentMap<Class<?>, Object> proxyCache = new ConcurrentHashMap<>();

    public RSocketProtocolClientHandler() {
        this.objectMapper = createObjectMapper();
    }

    public RSocketProtocolClientHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public ProtocolType protocol() {
        return ProtocolType.RSOCKET;
    }

    @Override
    public Object createProxy(ClientDefinition definition) {
        return proxyCache.computeIfAbsent(definition.interfaceType(), type -> {
            String address = (String) definition.attributes().get("address");
            RSocket rSocket = getOrCreateConnection(address);

            var handler = new RSocketClientProxy(rSocket, objectMapper);
            return Proxy.newProxyInstance(
                    type.getClassLoader(),
                    new Class<?>[]{type},
                    handler
            );
        });
    }

    @Override
    public void destroy() {
        connectionCache.values().forEach(RSocket::dispose);
        connectionCache.clear();
        proxyCache.clear();
    }

    private RSocket getOrCreateConnection(String address) {
        return connectionCache.computeIfAbsent(address, addr -> {
            String[] parts = addr.split(":");
            String host = parts[0];
            int port = parts.length > 1 ? Integer.parseInt(parts[1]) : 7000;

            return RSocketConnector.create()
                    .payloadDecoder(PayloadDecoder.ZERO_COPY)
                    .connect(TcpClientTransport.create(host, port))
                    .block();
        });
    }

    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }
}
