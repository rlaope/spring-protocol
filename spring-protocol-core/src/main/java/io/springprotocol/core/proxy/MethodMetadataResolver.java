package io.springprotocol.core.proxy;

import io.springprotocol.core.annotation.ProtocolMapping;

import java.lang.reflect.Method;

/**
 * Resolves {@link MethodMetadata} from a {@link Method} and its annotations.
 */
public final class MethodMetadataResolver {

    private MethodMetadataResolver() {
    }

    public static MethodMetadata resolve(Method method) {
        ProtocolMapping mapping = method.getAnnotation(ProtocolMapping.class);

        String mappedName = method.getName();
        String httpMethod = "";
        String query = "";
        String operationType = "";
        String route = "";
        String interaction = "";

        if (mapping != null) {
            if (!mapping.value().isEmpty()) {
                mappedName = mapping.value();
            }
            httpMethod = mapping.method();
            query = mapping.query();
            operationType = mapping.operationType();
            route = mapping.route().isEmpty() ? mappedName : mapping.route();
            interaction = mapping.interaction();
        }

        return new MethodMetadata(
                mappedName,
                httpMethod,
                query,
                operationType,
                route,
                interaction,
                method.getReturnType(),
                method.getParameterTypes()
        );
    }
}
