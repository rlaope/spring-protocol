package io.springprotocol.grpc.boot;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration properties for gRPC clients.
 *
 * <pre>
 * grpc:
 *   client:
 *     greeter-service:
 *       address: localhost:9090
 *     order-service:
 *       address: localhost:9091
 * </pre>
 */
@ConfigurationProperties(prefix = "grpc")
public class GrpcClientProperties {

    private Map<String, ServiceConfig> client = new HashMap<>();

    public Map<String, ServiceConfig> getClient() {
        return client;
    }

    public void setClient(Map<String, ServiceConfig> client) {
        this.client = client;
    }

    public static class ServiceConfig {

        private String address;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }
}
