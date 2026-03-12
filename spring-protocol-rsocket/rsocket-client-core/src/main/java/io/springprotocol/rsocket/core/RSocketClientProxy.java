package io.springprotocol.rsocket.core;

import io.springprotocol.core.proxy.AbstractClientProxy;
import io.springprotocol.core.proxy.MethodMetadata;

import java.lang.reflect.Method;

/**
 * RSocket-specific proxy that extends {@link AbstractClientProxy}.
 * Reads route from {@link MethodMetadata#route()} and interaction model
 * from {@link MethodMetadata#interaction()} (defaults to REQUEST_RESPONSE).
 */
public class RSocketClientProxy extends AbstractClientProxy {

    private static final String DEFAULT_INTERACTION = "REQUEST_RESPONSE";

    private final String address;

    public RSocketClientProxy(String address) {
        this.address = address;
    }

    @Override
    protected Object doInvoke(MethodMetadata metadata, Method method, Object[] args) throws Throwable {
        String route = metadata.route();
        String interaction = (metadata.interaction() != null && !metadata.interaction().isEmpty())
                ? metadata.interaction()
                : DEFAULT_INTERACTION;

        throw new UnsupportedOperationException("RSocket client execution not yet implemented");
    }

    @Override
    public String toString() {
        return "RSocketClientProxy[address=" + address + "]";
    }
}
