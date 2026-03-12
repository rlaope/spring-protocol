package io.springprotocol.grpc.core;

import io.springprotocol.core.annotation.ProtocolType;
import io.springprotocol.core.spi.ClientDefinition;
import io.springprotocol.core.spi.ProtocolClientHandler;
import io.springprotocol.grpc.core.channel.ChannelFactory;
import io.springprotocol.grpc.core.proxy.GrpcClientProxy;
import io.springprotocol.grpc.core.stub.BlockingStubCreator;
import io.springprotocol.grpc.core.stub.GrpcStubCreator;

import java.lang.reflect.Proxy;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class GrpcProtocolClientHandler implements ProtocolClientHandler {

    private final ChannelFactory channelFactory;
    private final ConcurrentMap<Class<?>, Object> proxyCache = new ConcurrentHashMap<>();
    private final GrpcStubCreator defaultStubCreator = new BlockingStubCreator();

    public GrpcProtocolClientHandler(ChannelFactory channelFactory) {
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
            Class<?> stubCreatorClass = (Class<?>) definition.attributes().get("stubCreator");

            if (grpcClass == null) {
                throw new IllegalStateException(
                        "grpcClass is required for gRPC protocol on " + type.getName()
                                + ". Set grpcClass in @SpringClient annotation.");
            }

            GrpcStubCreator creator = resolveStubCreator(stubCreatorClass);
            var channel = channelFactory.getChannel(address);
            var stub = creator.create(grpcClass, channel);
            var handler = new GrpcClientProxy(stub);
            return Proxy.newProxyInstance(
                    type.getClassLoader(),
                    new Class<?>[]{type},
                    handler
            );
        });
    }

    private GrpcStubCreator resolveStubCreator(Class<?> stubCreatorClass) {
        if (stubCreatorClass == null || stubCreatorClass == void.class) {
            return defaultStubCreator;
        }
        try {
            return (GrpcStubCreator) stubCreatorClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to instantiate stubCreator: " + stubCreatorClass.getName()
                            + ". Ensure it implements GrpcStubCreator and has a no-arg constructor.", e);
        }
    }

    @Override
    public void destroy() {
        channelFactory.shutdownAll();
        proxyCache.clear();
    }
}
