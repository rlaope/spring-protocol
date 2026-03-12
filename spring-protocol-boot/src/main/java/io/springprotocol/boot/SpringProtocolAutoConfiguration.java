package io.springprotocol.boot;

import io.springprotocol.core.spi.ProtocolClientHandler;
import io.springprotocol.core.spi.ProtocolRegistry;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Unified auto-configuration that wires the ProtocolRegistry with all
 * available ProtocolClientHandler beans on the classpath.
 * Each protocol module registers its own handler via its own auto-configuration.
 */
@Configuration
@EnableConfigurationProperties(SpringProtocolProperties.class)
public class SpringProtocolAutoConfiguration implements DisposableBean {

    private ProtocolRegistry protocolRegistry;

    @Bean
    @ConditionalOnMissingBean
    public ProtocolRegistry protocolRegistry(List<ProtocolClientHandler> handlers) {
        ProtocolRegistry registry = new ProtocolRegistry();
        handlers.forEach(registry::register);
        this.protocolRegistry = registry;
        return registry;
    }

    @Override
    public void destroy() {
        if (protocolRegistry != null) {
            protocolRegistry.destroyAll();
        }
    }
}
