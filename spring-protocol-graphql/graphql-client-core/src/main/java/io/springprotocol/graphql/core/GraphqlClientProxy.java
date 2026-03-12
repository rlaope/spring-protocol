package io.springprotocol.graphql.core;

import io.springprotocol.core.proxy.AbstractClientProxy;
import io.springprotocol.core.proxy.MethodMetadata;

import java.lang.reflect.Method;
import java.net.http.HttpClient;

/**
 * GraphQL implementation of {@link AbstractClientProxy}.
 * Reads the GraphQL query string from {@link MethodMetadata#query()} and
 * the operation name from {@link MethodMetadata#mappedName()}.
 * Full request execution is pending.
 */
public class GraphqlClientProxy extends AbstractClientProxy {

    private final String endpoint;
    private final HttpClient httpClient;

    public GraphqlClientProxy(String endpoint, HttpClient httpClient) {
        this.endpoint = endpoint;
        this.httpClient = httpClient;
    }

    @Override
    protected Object doInvoke(MethodMetadata metadata, Method method, Object[] args) throws Throwable {
        String query = metadata.query();
        String operationName = metadata.mappedName();

        // TODO: build GraphQL request body, send via httpClient, deserialize response
        throw new UnsupportedOperationException("GraphQL client execution not yet implemented");
    }
}
