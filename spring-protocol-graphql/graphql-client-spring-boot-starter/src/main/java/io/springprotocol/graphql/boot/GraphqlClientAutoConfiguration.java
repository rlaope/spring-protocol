package io.springprotocol.graphql.boot;

import io.springprotocol.core.spi.ProtocolClientHandler;
import io.springprotocol.graphql.core.GraphqlProtocolClientHandler;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Auto-configuration for GraphQL client infrastructure beans.
 * Registers {@link GraphqlProtocolClientHandler} as a {@link ProtocolClientHandler}
 * for the unified Spring Protocol framework.
 */
@Configuration
public class GraphqlClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(GraphqlProtocolClientHandler.class)
    public ProtocolClientHandler graphqlProtocolClientHandler() {
        return new GraphqlProtocolClientHandler();
    }
}
