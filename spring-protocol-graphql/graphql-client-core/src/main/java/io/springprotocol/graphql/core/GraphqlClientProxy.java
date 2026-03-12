package io.springprotocol.graphql.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.springprotocol.core.proxy.AbstractClientProxy;
import io.springprotocol.core.proxy.MethodMetadata;

import java.lang.reflect.Method;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class GraphqlClientProxy extends AbstractClientProxy {

    private final String endpoint;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public GraphqlClientProxy(String endpoint, HttpClient httpClient, ObjectMapper objectMapper) {
        this.endpoint = endpoint;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    @Override
    protected Object doInvoke(MethodMetadata metadata, Method method, Object[] args) throws Throwable {
        String query = metadata.query();
        if (query == null || query.isBlank()) {
            throw new IllegalStateException(
                    "No GraphQL query defined for method " + method.getName()
                            + ". Use @ProtocolMapping(query = \"...\")");
        }

        String operationType = metadata.operationType();
        String operationName = metadata.mappedName();

        Map<String, Object> variables = buildVariables(method, args);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", query);
        if (!variables.isEmpty()) {
            requestBody.put("variables", variables);
        }
        if (operationName != null && !operationName.equals(method.getName())) {
            requestBody.put("operationName", operationName);
        }

        String json = objectMapper.writeValueAsString(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 400) {
            throw new GraphqlClientException(endpoint, response.statusCode(), response.body());
        }

        JsonNode root = objectMapper.readTree(response.body());

        JsonNode errors = root.get("errors");
        if (errors != null && errors.isArray() && !errors.isEmpty()) {
            throw new GraphqlClientException(endpoint, errors.toString());
        }

        JsonNode data = root.get("data");
        if (data == null || data.isNull()) {
            return null;
        }

        Class<?> returnType = method.getReturnType();
        if (returnType == void.class || returnType == Void.class) {
            return null;
        }
        if (returnType == String.class) {
            return data.toString();
        }
        if (returnType == JsonNode.class) {
            return data;
        }

        // If data has a single field, unwrap it
        if (data.isObject() && data.size() == 1) {
            JsonNode inner = data.elements().next();
            return objectMapper.treeToValue(inner, returnType);
        }

        return objectMapper.treeToValue(data, returnType);
    }

    private Map<String, Object> buildVariables(Method method, Object[] args) {
        Map<String, Object> variables = new HashMap<>();
        if (args == null || args.length == 0) {
            return variables;
        }
        java.lang.reflect.Parameter[] params = method.getParameters();
        for (int i = 0; i < params.length; i++) {
            variables.put(params[i].getName(), args[i]);
        }
        return variables;
    }

    @Override
    public String toString() {
        return "GraphqlClientProxy[endpoint=" + endpoint + "]";
    }
}
