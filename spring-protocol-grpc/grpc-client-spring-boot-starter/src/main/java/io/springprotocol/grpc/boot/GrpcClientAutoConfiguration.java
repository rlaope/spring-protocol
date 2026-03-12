package io.springprotocol.grpc.boot;

import io.springprotocol.core.spi.ProtocolClientHandler;
import io.springprotocol.grpc.core.GrpcProtocolClientHandler;
import io.springprotocol.grpc.core.channel.CachingChannelFactory;
import io.springprotocol.grpc.core.channel.ChannelFactory;
import io.springprotocol.grpc.core.stub.ReflectiveStubFactory;
import io.springprotocol.grpc.core.stub.StubFactory;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(GrpcClientProperties.class)
public class GrpcClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public StubFactory grpcStubFactory() {
        return new ReflectiveStubFactory();
    }

    @Bean
    @ConditionalOnMissingBean
    public ChannelFactory grpcChannelFactory() {
        return new CachingChannelFactory();
    }

    @Bean
    @ConditionalOnMissingBean(name = "grpcProtocolClientHandler")
    public ProtocolClientHandler grpcProtocolClientHandler(StubFactory stubFactory,
                                                            ChannelFactory channelFactory) {
        return new GrpcProtocolClientHandler(stubFactory, channelFactory);
    }
}
