package io.springprotocol.grpc.core.proxy;

import io.springprotocol.grpc.core.annotation.GrpcMapping;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * JDK Dynamic Proxy InvocationHandler that bridges interface method calls
 * to the actual gRPC blocking stub.
 *
 * <p>Method metadata (stub method lookup) is cached in a ConcurrentHashMap
 * so reflection only happens once per interface method.</p>
 */
public class GrpcClientProxy implements InvocationHandler {

    private final Object stub;
    private final ConcurrentMap<Method, GrpcMethodMetadata> metadataCache = new ConcurrentHashMap<>();

    public GrpcClientProxy(Object stub) {
        this.stub = stub;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // Handle Object methods (toString, equals, hashCode)
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(this, args);
        }

        GrpcMethodMetadata metadata = metadataCache.computeIfAbsent(method, this::resolveMetadata);
        return metadata.stubMethod().invoke(stub, args);
    }

    private GrpcMethodMetadata resolveMetadata(Method interfaceMethod) {
        String stubMethodName = resolveStubMethodName(interfaceMethod);
        Class<?>[] paramTypes = interfaceMethod.getParameterTypes();

        try {
            Method stubMethod = stub.getClass().getMethod(stubMethodName, paramTypes);
            Class<?> requestType = paramTypes.length > 0 ? paramTypes[0] : Void.class;
            return new GrpcMethodMetadata(stubMethod, requestType);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(
                    "No method '" + stubMethodName + "' found on stub " + stub.getClass().getName()
                            + " matching parameters of " + interfaceMethod, e);
        }
    }

    private String resolveStubMethodName(Method method) {
        GrpcMapping mapping = method.getAnnotation(GrpcMapping.class);
        if (mapping != null && !mapping.value().isEmpty()) {
            return mapping.value();
        }
        return method.getName();
    }

    @Override
    public String toString() {
        return "GrpcClientProxy[stub=" + stub.getClass().getSimpleName() + "]";
    }
}
