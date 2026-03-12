package io.springprotocol.core.spi;

import io.springprotocol.core.annotation.ProtocolType;

/**
 * SPI interface for protocol-specific client handling.
 * Each protocol (gRPC, GraphQL, REST, RSocket) provides an implementation
 * that knows how to create proxies for its protocol.
 */
public interface ProtocolClientHandler {

    /**
     * The protocol type this handler supports.
     */
    ProtocolType protocol();

    /**
     * Creates a proxy object implementing the client interface.
     *
     * @param definition the client definition with all metadata
     * @return a proxy implementing the client interface
     */
    Object createProxy(ClientDefinition definition);

    /**
     * Releases resources held by this handler (channels, connections, etc.).
     */
    void destroy();
}
