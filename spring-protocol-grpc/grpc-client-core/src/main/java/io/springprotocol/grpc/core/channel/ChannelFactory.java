package io.springprotocol.grpc.core.channel;

import io.grpc.ManagedChannel;

/**
 * Creates and manages gRPC ManagedChannel instances.
 * Implementations should handle channel sharing per service and lifecycle management.
 */
public interface ChannelFactory {

    /**
     * Returns a ManagedChannel for the given target address.
     * Implementations should reuse channels for the same address.
     *
     * @param address the target address (e.g., "localhost:9090")
     * @return a managed channel
     */
    ManagedChannel getChannel(String address);

    /**
     * Shuts down all managed channels gracefully.
     */
    void shutdownAll();
}
