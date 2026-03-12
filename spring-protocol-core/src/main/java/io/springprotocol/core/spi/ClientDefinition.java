package io.springprotocol.core.spi;

import java.util.Map;

/**
 * Immutable definition carrying all information needed to create a protocol client proxy.
 */
public record ClientDefinition(
        Class<?> interfaceType,
        String protocol,
        String serviceId,
        Map<String, Object> attributes
) {

    public ClientDefinition {
        attributes = Map.copyOf(attributes);
    }

    /**
     * Returns a new definition with additional attributes merged in.
     */
    public ClientDefinition withAttributes(Map<String, Object> extra) {
        var merged = new java.util.HashMap<>(attributes);
        merged.putAll(extra);
        return new ClientDefinition(interfaceType, protocol, serviceId, merged);
    }
}
