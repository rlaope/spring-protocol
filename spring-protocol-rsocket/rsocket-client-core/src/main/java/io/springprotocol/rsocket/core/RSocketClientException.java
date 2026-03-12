package io.springprotocol.rsocket.core;

public class RSocketClientException extends RuntimeException {

    public RSocketClientException(String message) {
        super(message);
    }

    public RSocketClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
