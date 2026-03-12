package io.springprotocol.grpc.core;

import io.springprotocol.core.annotation.ProtocolType;
import io.springprotocol.core.spi.ClientDefinition;
import io.springprotocol.core.spi.ProtocolClientHandler;
import io.springprotocol.grpc.core.channel.ChannelFactory;
import io.springprotocol.grpc.core.proxy.GrpcClientProxy;
import io.springprotocol.grpc.core.stub.StubFactory;

import java.lang.reflect.Proxy;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * gRPC implementation of {@link ProtocolClientHandler}.
 * Creates JDK dynamic proxies backed by gRPC blocking stubs.
 */
public class GrpcProtocolClientHandler implements ProtocolClientHandler {

    private final StubFactory stubFactory;
    private final ChannelFactory channelFactory;
    private final ConcurrentMap<Class<?>, Object> proxyCache = new ConcurrentHashMap<>();

    public GrpcProtocolClientHandler(StubFactory stubFactory, ChannelFactory channelFactory) {
        this.stubFactory = stubFactory;
        this.channelFactory = channelFactory;
    }

    @Override
    public ProtocolType protocol() {
        return ProtocolType.GRPC;
    }

    @Override
    public Object createProxy(ClientDefinition definition) {
        return proxyCache.computeIfAbsent(definition.interfaceType(), type -> {
            Class<?> grpcClass = (Class<?>) definition.attributes().get("grpcClass");
            String address = (String) definition.attributes().get("address");

            if (grpcClass == null) {
                throw new IllegalStateException(
                        "grpcClass is required for gRPC protocol on " + type.getName()
                                + ". Set grpcClass in @SpringClient annotation.");
            }

            var channel = channelFactory.getChannel(address);
            var stub = stubFactory.createStub(grpcClass, channel);
            var handler = new GrpcClientProxy(stub);
            return Proxy.newProxyInstance(
                    type.getClassLoader(),
                    new Class<?>[]{type},
                    handler
            );
        });
    }

    @Override
    public void destroy() {
        channelFactory.shutdownAll();
        proxyCache.clear();
    }
}
