package io.springprotocol.rsocket.core;

import io.springprotocol.core.annotation.ProtocolType;
import io.springprotocol.core.spi.ClientDefinition;
import io.springprotocol.core.spi.ProtocolClientHandler;

import java.lang.reflect.Proxy;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * RSocket implementation of {@link ProtocolClientHandler}.
 * Creates JDK dynamic proxies backed by RSocket connections.
 */
public class RSocketProtocolClientHandler implements ProtocolClientHandler {

    private final ConcurrentMap<Class<?>, Object> proxyCache = new ConcurrentHashMap<>();

    @Override
    public ProtocolType protocol() {
        return ProtocolType.RSOCKET;
    }

    @Override
    public Object createProxy(ClientDefinition definition) {
        return proxyCache.computeIfAbsent(definition.interfaceType(), type -> {
            String address = (String) definition.attributes().get("address");
            var handler = new RSocketClientProxy(address);
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
}
