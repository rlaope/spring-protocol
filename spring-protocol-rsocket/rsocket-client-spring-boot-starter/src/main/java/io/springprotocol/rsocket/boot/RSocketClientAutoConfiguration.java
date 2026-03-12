package io.springprotocol.rsocket.boot;

import io.springprotocol.core.spi.ProtocolClientHandler;
import io.springprotocol.rsocket.core.RSocketProtocolClientHandler;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Auto-configuration for RSocket client infrastructure beans.
 * Registers {@link RSocketProtocolClientHandler} as a {@link ProtocolClientHandler}
 * for the unified Spring Protocol framework.
 */
@Configuration
public class RSocketClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "rsocketProtocolClientHandler")
    public ProtocolClientHandler rsocketProtocolClientHandler() {
        return new RSocketProtocolClientHandler();
    }
}
