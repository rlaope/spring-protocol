package io.springprotocol.grpc.core.proxy;

import io.springprotocol.core.annotation.ProtocolMapping;
import io.springprotocol.core.proxy.AbstractClientProxy;
import io.springprotocol.core.proxy.MethodMetadata;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class GrpcClientProxy extends AbstractClientProxy {

    private final Object stub;
    private final ConcurrentMap<String, Method> stubMethodCache = new ConcurrentHashMap<>();

    public GrpcClientProxy(Object stub) {
        this.stub = stub;
    }

    @Override
    protected Object doInvoke(MethodMetadata metadata, Method method, Object[] args) throws Throwable {
        String stubMethodName = resolveStubMethodName(metadata, method);
        Method stubMethod = stubMethodCache.computeIfAbsent(
                stubMethodName + ":" + method.getParameterCount(),
                key -> findStubMethod(stubMethodName, method.getParameterTypes())
        );
        return stubMethod.invoke(stub, args);
    }

    private String resolveStubMethodName(MethodMetadata metadata, Method method) {
        ProtocolMapping protocolMapping = method.getAnnotation(ProtocolMapping.class);
        if (protocolMapping != null && !protocolMapping.value().isEmpty()) {
            return metadata.mappedName();
        }
        return method.getName();
    }

    private Method findStubMethod(String name, Class<?>[] paramTypes) {
        try {
            return stub.getClass().getMethod(name, paramTypes);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(
                    "No method '" + name + "' found on stub " + stub.getClass().getName(), e);
        }
    }

    @Override
    public String toString() {
        return "GrpcClientProxy[stub=" + stub.getClass().getSimpleName() + "]";
    }
}
