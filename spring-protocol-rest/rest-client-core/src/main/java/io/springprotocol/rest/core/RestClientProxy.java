package io.springprotocol.rest.core;

import io.springprotocol.core.proxy.AbstractClientProxy;
import io.springprotocol.core.proxy.MethodMetadata;

import java.lang.reflect.Method;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

/**
 * REST implementation of {@link AbstractClientProxy}.
 * Builds an HTTP request from {@link MethodMetadata} and delegates to
 * {@link HttpClient}. Full response handling is pending.
 */
public class RestClientProxy extends AbstractClientProxy {

    private final String baseUrl;
    private final HttpClient httpClient;

    public RestClientProxy(String baseUrl, HttpClient httpClient) {
        this.baseUrl = baseUrl;
        this.httpClient = httpClient;
    }

    @Override
    protected Object doInvoke(MethodMetadata metadata, Method method, Object[] args) throws Throwable {
        String path = metadata.mappedName() != null ? metadata.mappedName() : "";
        String httpMethod = metadata.method() != null && !metadata.method().isBlank()
                ? metadata.method().toUpperCase()
                : "GET";

        URI uri = URI.create(baseUrl + path);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .method(httpMethod, HttpRequest.BodyPublishers.noBody())
                .build();

        // TODO: send request via httpClient and deserialize response
        throw new UnsupportedOperationException(
                "REST client full implementation pending. Built request: " + request);
    }
}
