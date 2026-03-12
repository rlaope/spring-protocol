package io.springprotocol.grpc.boot;

import io.springprotocol.grpc.core.channel.CachingChannelFactory;
import io.springprotocol.grpc.core.channel.ChannelFactory;
import io.springprotocol.grpc.core.proxy.GrpcClientProxyFactory;
import io.springprotocol.grpc.core.stub.ReflectiveStubFactory;
import io.springprotocol.grpc.core.stub.StubFactory;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Auto-configuration for gRPC client infrastructure beans.
 * Provides sensible defaults that can be overridden by user-defined beans.
 */
@Configuration
@EnableConfigurationProperties(GrpcClientProperties.class)
public class GrpcClientAutoConfiguration implements DisposableBean {

    private ChannelFactory channelFactory;

    @Bean
    @ConditionalOnMissingBean
    public StubFactory grpcStubFactory() {
        return new ReflectiveStubFactory();
    }

    @Bean
    @ConditionalOnMissingBean
    public ChannelFactory grpcChannelFactory() {
        this.channelFactory = new CachingChannelFactory();
        return this.channelFactory;
    }

    @Bean
    @ConditionalOnMissingBean
    public GrpcClientProxyFactory grpcClientProxyFactory(StubFactory stubFactory,
                                                          ChannelFactory channelFactory) {
        return new GrpcClientProxyFactory(stubFactory, channelFactory);
    }

    @Override
    public void destroy() {
        if (channelFactory != null) {
            channelFactory.shutdownAll();
        }
    }
}
