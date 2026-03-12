package io.springprotocol.spring.factory;

import io.springprotocol.core.annotation.ProtocolType;
import io.springprotocol.core.spi.ClientDefinition;
import io.springprotocol.core.spi.ProtocolClientHandler;
import io.springprotocol.core.spi.ProtocolRegistry;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

/**
 * Spring FactoryBean that produces a protocol client proxy for a given interface.
 * Resolves connection info from Environment and delegates to the appropriate
 * ProtocolClientHandler via ProtocolRegistry.
 */
public class SpringClientFactoryBean<T> implements FactoryBean<T> {

    private Class<T> interfaceType;
    private ProtocolType protocol;
    private String serviceId;
    private Map<String, Object> attributes = Map.of();

    @Autowired
    private ProtocolRegistry protocolRegistry;

    @Autowired
    private Environment environment;

    @Override
    @SuppressWarnings("unchecked")
    public T getObject() {
        String connectionInfo = resolveConnectionInfo();

        Map<String, Object> enriched = new HashMap<>(attributes);
        enriched.put("address", connectionInfo);

        ClientDefinition definition = new ClientDefinition(
                interfaceType,
                protocol.name().toLowerCase(),
                serviceId,
                enriched
        );

        ProtocolClientHandler handler = protocolRegistry.getHandler(protocol);
        return (T) handler.createProxy(definition);
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceType;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    private String resolveConnectionInfo() {
        String protocolName = protocol.name().toLowerCase();

        // New format: spring.protocol.grpc.clients.greeter-service.address
        String key = "spring.protocol." + protocolName + ".clients." + serviceId + ".address";
        String value = environment.getProperty(key);

        // Try url variant (for REST, GraphQL)
        if (value == null) {
            key = "spring.protocol." + protocolName + ".clients." + serviceId + ".url";
            value = environment.getProperty(key);
        }

        // Legacy fallback for gRPC
        if (value == null && protocol == ProtocolType.GRPC) {
            key = "grpc.client." + serviceId + ".address";
            value = environment.getProperty(key);
        }

        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "No connection info configured for service '" + serviceId
                            + "' (protocol=" + protocolName + "). "
                            + "Set 'spring.protocol." + protocolName + ".clients." + serviceId + ".address' "
                            + "in your configuration.");
        }
        return value;
    }

    public void setInterfaceType(Class<T> interfaceType) {
        this.interfaceType = interfaceType;
    }

    public void setProtocol(ProtocolType protocol) {
        this.protocol = protocol;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
}
