package io.springprotocol.grpc.core.stub;

import io.grpc.Channel;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Reflection-based implementation that locates {@code newBlockingStub(Channel)}
 * on the generated gRPC class and caches the factory method for reuse.
 */
public class ReflectiveStubFactory implements StubFactory {

    private static final String NEW_BLOCKING_STUB = "newBlockingStub";

    private final ConcurrentMap<Class<?>, Method> factoryMethodCache = new ConcurrentHashMap<>();

    @Override
    public Object createStub(Class<?> grpcClass, Channel channel) {
        Method factoryMethod = factoryMethodCache.computeIfAbsent(grpcClass, this::resolveFactoryMethod);
        try {
            return factoryMethod.invoke(null, channel);
        } catch (Exception e) {
            throw new StubCreationException(
                    "Failed to create blocking stub for " + grpcClass.getName(), e);
        }
    }

    private Method resolveFactoryMethod(Class<?> grpcClass) {
        try {
            Method method = grpcClass.getMethod(NEW_BLOCKING_STUB, Channel.class);
            if (!java.lang.reflect.Modifier.isStatic(method.getModifiers())) {
                throw new StubCreationException(
                        NEW_BLOCKING_STUB + " must be a static method on " + grpcClass.getName());
            }
            return method;
        } catch (NoSuchMethodException e) {
            throw new StubCreationException(
                    "No " + NEW_BLOCKING_STUB + "(Channel) method found on " + grpcClass.getName(), e);
        }
    }
}
