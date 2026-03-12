package io.springprotocol.graphql.core;

public class GraphqlClientException extends RuntimeException {

    private final String endpoint;
    private final int statusCode;
    private final String responseBody;

    public GraphqlClientException(String endpoint, int statusCode, String responseBody) {
        super(String.format("GraphQL call failed: %s returned %d", endpoint, statusCode));
        this.endpoint = endpoint;
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public GraphqlClientException(String endpoint, String errors) {
        super(String.format("GraphQL errors from %s: %s", endpoint, errors));
        this.endpoint = endpoint;
        this.statusCode = 200;
        this.responseBody = errors;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
