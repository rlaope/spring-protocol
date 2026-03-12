package io.springprotocol.core.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Base class for all protocol client proxies.
 * Handles Object method delegation and caches {@link MethodMetadata} per method
 * so annotation resolution happens only once.
 */
public abstract class AbstractClientProxy implements InvocationHandler {

    private final ConcurrentMap<Method, MethodMetadata> metadataCache = new ConcurrentHashMap<>();

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(this, args);
        }

        MethodMetadata metadata = metadataCache.computeIfAbsent(method, MethodMetadataResolver::resolve);
        return doInvoke(metadata, method, args);
    }

    /**
     * Protocol-specific invocation logic.
     *
     * @param metadata cached method metadata from {@code @ProtocolMapping}
     * @param method   the original interface method
     * @param args     the method arguments
     * @return the invocation result
     */
    protected abstract Object doInvoke(MethodMetadata metadata, Method method, Object[] args) throws Throwable;
}
