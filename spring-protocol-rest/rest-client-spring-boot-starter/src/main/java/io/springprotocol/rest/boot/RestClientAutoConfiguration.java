package io.springprotocol.rest.boot;

import io.springprotocol.core.spi.ProtocolClientHandler;
import io.springprotocol.rest.core.RestProtocolClientHandler;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Auto-configuration for REST client infrastructure beans.
 * Registers {@link RestProtocolClientHandler} as a {@link ProtocolClientHandler}
 * for the unified Spring Protocol framework.
 */
@Configuration
public class RestClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(RestProtocolClientHandler.class)
    public ProtocolClientHandler restProtocolClientHandler() {
        return new RestProtocolClientHandler();
    }
}
