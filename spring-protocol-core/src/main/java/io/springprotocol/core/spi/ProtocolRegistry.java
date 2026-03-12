package io.springprotocol.core.spi;

import io.springprotocol.core.annotation.ProtocolType;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Registry that maps protocol types to their handlers.
 * Handlers are registered at startup and looked up during proxy creation.
 */
public class ProtocolRegistry {

    private final ConcurrentMap<ProtocolType, ProtocolClientHandler> handlers = new ConcurrentHashMap<>();

    public void register(ProtocolClientHandler handler) {
        ProtocolClientHandler existing = handlers.putIfAbsent(handler.protocol(), handler);
        if (existing != null) {
            throw new IllegalStateException(
                    "Duplicate handler for protocol " + handler.protocol()
                            + ": " + existing.getClass().getName()
                            + " and " + handler.getClass().getName());
        }
    }

    public ProtocolClientHandler getHandler(ProtocolType protocol) {
        ProtocolClientHandler handler = handlers.get(protocol);
        if (handler == null) {
            throw new IllegalStateException(
                    "No handler registered for protocol: " + protocol
                            + ". Ensure the corresponding protocol module is on the classpath.");
        }
        return handler;
    }

    public void destroyAll() {
        handlers.values().forEach(ProtocolClientHandler::destroy);
        handlers.clear();
    }
}
