package io.springprotocol.grpc.core.stub;

public class StubCreationException extends RuntimeException {

    public StubCreationException(String message) {
        super(message);
    }

    public StubCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
