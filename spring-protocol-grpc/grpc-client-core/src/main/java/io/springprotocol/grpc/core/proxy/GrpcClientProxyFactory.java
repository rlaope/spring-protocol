package io.springprotocol.grpc.core.proxy;

import io.springprotocol.grpc.core.channel.ChannelFactory;
import io.springprotocol.grpc.core.stub.StubFactory;

import java.lang.reflect.Proxy;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Factory that creates and caches JDK dynamic proxy instances
 * for gRPC client interfaces.
 */
public class GrpcClientProxyFactory {

    private final StubFactory stubFactory;
    private final ChannelFactory channelFactory;
    private final ConcurrentMap<Class<?>, Object> proxyCache = new ConcurrentHashMap<>();

    public GrpcClientProxyFactory(StubFactory stubFactory, ChannelFactory channelFactory) {
        this.stubFactory = stubFactory;
        this.channelFactory = channelFactory;
    }

    /**
     * Creates (or returns cached) a proxy for the given interface.
     *
     * @param interfaceType the gRPC client interface
     * @param grpcClass     the generated gRPC class
     * @param address       the target service address
     * @return a proxy implementing the interface
     */
    @SuppressWarnings("unchecked")
    public <T> T createProxy(Class<T> interfaceType, Class<?> grpcClass, String address) {
        return (T) proxyCache.computeIfAbsent(interfaceType, type -> {
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
}
