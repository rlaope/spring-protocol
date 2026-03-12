package io.springprotocol.rest.core;

import java.net.URI;

public class RestClientException extends RuntimeException {

    private final String method;
    private final URI uri;
    private final int statusCode;
    private final String responseBody;

    public RestClientException(String method, URI uri, int statusCode, String responseBody) {
        super(String.format("REST call failed: %s %s returned %d", method, uri, statusCode));
        this.method = method;
        this.uri = uri;
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public String getMethod() {
        return method;
    }

    public URI getUri() {
        return uri;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
