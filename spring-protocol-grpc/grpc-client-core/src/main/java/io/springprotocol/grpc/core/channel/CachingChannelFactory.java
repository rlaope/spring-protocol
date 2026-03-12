package io.springprotocol.grpc.core.channel;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * Caches ManagedChannel instances per address so that multiple stubs
 * targeting the same service share a single channel.
 */
public class CachingChannelFactory implements ChannelFactory {

    private final ConcurrentMap<String, ManagedChannel> channels = new ConcurrentHashMap<>();

    @Override
    public ManagedChannel getChannel(String address) {
        return channels.computeIfAbsent(address, this::createChannel);
    }

    @Override
    public void shutdownAll() {
        channels.values().forEach(channel -> {
            channel.shutdown();
            try {
                if (!channel.awaitTermination(5, TimeUnit.SECONDS)) {
                    channel.shutdownNow();
                }
            } catch (InterruptedException e) {
                channel.shutdownNow();
                Thread.currentThread().interrupt();
            }
        });
        channels.clear();
    }

    private ManagedChannel createChannel(String address) {
        return ManagedChannelBuilder.forTarget(address)
                .usePlaintext()
                .build();
    }
}
