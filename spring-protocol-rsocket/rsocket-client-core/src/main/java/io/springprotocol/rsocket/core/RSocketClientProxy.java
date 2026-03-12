package io.springprotocol.rsocket.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.util.DefaultPayload;
import io.springprotocol.core.proxy.AbstractClientProxy;
import io.springprotocol.core.proxy.MethodMetadata;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class RSocketClientProxy extends AbstractClientProxy {

    private static final String REQUEST_RESPONSE = "REQUEST_RESPONSE";
    private static final String FIRE_AND_FORGET = "FIRE_AND_FORGET";
    private static final String REQUEST_STREAM = "REQUEST_STREAM";

    private final RSocket rSocket;
    private final ObjectMapper objectMapper;

    public RSocketClientProxy(RSocket rSocket, ObjectMapper objectMapper) {
        this.rSocket = rSocket;
        this.objectMapper = objectMapper;
    }

    @Override
    protected Object doInvoke(MethodMetadata metadata, Method method, Object[] args) throws Throwable {
        String route = metadata.route();
        String interaction = (metadata.interaction() != null && !metadata.interaction().isEmpty())
                ? metadata.interaction().toUpperCase()
                : REQUEST_RESPONSE;

        Payload payload = buildPayload(route, args);

        return switch (interaction) {
            case FIRE_AND_FORGET -> {
                rSocket.fireAndForget(payload).block();
                yield null;
            }
            case REQUEST_STREAM -> {
                Class<?> elementType = resolveFluxElementType(method);
                Flux<Payload> stream = rSocket.requestStream(payload);
                yield stream.map(p -> deserialize(p, elementType));
            }
            default -> {
                // REQUEST_RESPONSE
                Payload response = rSocket.requestResponse(payload).block();
                if (response == null) {
                    yield null;
                }
                Class<?> returnType = method.getReturnType();
                if (returnType == void.class || returnType == Void.class) {
                    response.release();
                    yield null;
                }
                if (returnType == String.class) {
                    yield response.getDataUtf8();
                }
                if (Mono.class.isAssignableFrom(returnType)) {
                    Class<?> monoType = resolveMonoElementType(method);
                    yield Mono.just(deserialize(response, monoType));
                }
                yield deserialize(response, returnType);
            }
        };
    }

    private Payload buildPayload(String route, Object[] args) throws Exception {
        String data = "";
        if (args != null && args.length > 0) {
            data = objectMapper.writeValueAsString(args[0]);
        }
        String metadata = route != null ? route : "";
        return DefaultPayload.create(data, metadata);
    }

    private Object deserialize(Payload payload, Class<?> targetType) {
        try {
            String json = payload.getDataUtf8();
            if (targetType == String.class) {
                return json;
            }
            return objectMapper.readValue(json, targetType);
        } catch (Exception e) {
            throw new RSocketClientException("Failed to deserialize RSocket response", e);
        }
    }

    private Class<?> resolveFluxElementType(Method method) {
        Type returnType = method.getGenericReturnType();
        if (returnType instanceof ParameterizedType pt) {
            Type[] typeArgs = pt.getActualTypeArguments();
            if (typeArgs.length > 0 && typeArgs[0] instanceof Class<?> c) {
                return c;
            }
        }
        return Object.class;
    }

    private Class<?> resolveMonoElementType(Method method) {
        Type returnType = method.getGenericReturnType();
        if (returnType instanceof ParameterizedType pt) {
            Type[] typeArgs = pt.getActualTypeArguments();
            if (typeArgs.length > 0 && typeArgs[0] instanceof Class<?> c) {
                return c;
            }
        }
        return Object.class;
    }

    @Override
    public String toString() {
        return "RSocketClientProxy[rSocket=" + rSocket + "]";
    }
}
