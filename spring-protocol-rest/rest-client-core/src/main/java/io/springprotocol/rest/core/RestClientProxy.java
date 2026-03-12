package io.springprotocol.rest.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.springprotocol.core.proxy.AbstractClientProxy;
import io.springprotocol.core.proxy.MethodMetadata;

import java.lang.reflect.Method;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RestClientProxy extends AbstractClientProxy {

    private static final Pattern PATH_VARIABLE_PATTERN = Pattern.compile("\\{(\\w+)}");

    private final String baseUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public RestClientProxy(String baseUrl, HttpClient httpClient, ObjectMapper objectMapper) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    @Override
    protected Object doInvoke(MethodMetadata metadata, Method method, Object[] args) throws Throwable {
        String path = metadata.mappedName() != null ? metadata.mappedName() : "";
        String httpMethod = metadata.method() != null && !metadata.method().isBlank()
                ? metadata.method().toUpperCase()
                : "GET";

        String resolvedPath = resolvePath(path, method, args);
        URI uri = URI.create(baseUrl + resolvedPath);

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(uri);
        requestBuilder.header("Content-Type", "application/json");
        requestBuilder.header("Accept", "application/json");

        Object body = resolveBody(httpMethod, method, args, path);

        if (body != null) {
            String json = objectMapper.writeValueAsString(body);
            requestBuilder.method(httpMethod, HttpRequest.BodyPublishers.ofString(json));
        } else {
            requestBuilder.method(httpMethod, HttpRequest.BodyPublishers.noBody());
        }

        HttpResponse<String> response = httpClient.send(
                requestBuilder.build(),
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() >= 400) {
            throw new RestClientException(httpMethod, uri, response.statusCode(), response.body());
        }

        Class<?> returnType = method.getReturnType();
        if (returnType == void.class || returnType == Void.class) {
            return null;
        }
        if (returnType == String.class) {
            return response.body();
        }

        return objectMapper.readValue(response.body(), returnType);
    }

    private String resolvePath(String path, Method method, Object[] args) {
        if (args == null || args.length == 0) {
            return path;
        }

        java.lang.reflect.Parameter[] params = method.getParameters();
        String resolved = path;
        Matcher matcher = PATH_VARIABLE_PATTERN.matcher(path);
        int pathVarIndex = 0;

        while (matcher.find()) {
            String varName = matcher.group(1);
            if (pathVarIndex < args.length) {
                resolved = resolved.replace("{" + varName + "}", String.valueOf(args[pathVarIndex]));
                pathVarIndex++;
            }
        }
        return resolved;
    }

    private Object resolveBody(String httpMethod, Method method, Object[] args, String path) {
        if (args == null || args.length == 0) {
            return null;
        }
        if ("GET".equals(httpMethod) || "DELETE".equals(httpMethod)) {
            return null;
        }

        // Count path variables consumed
        Matcher matcher = PATH_VARIABLE_PATTERN.matcher(path);
        int pathVarCount = 0;
        while (matcher.find()) {
            pathVarCount++;
        }

        // The arg after path variables is the body
        if (pathVarCount < args.length) {
            return args[pathVarCount];
        }

        return null;
    }

    @Override
    public String toString() {
        return "RestClientProxy[baseUrl=" + baseUrl + "]";
    }
}
