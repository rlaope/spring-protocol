package io.springprotocol.boot;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Unified configuration properties for all Spring Protocol clients.
 *
 * <pre>
 * spring:
 *   protocol:
 *     grpc:
 *       clients:
 *         greeter-service:
 *           address: localhost:9090
 *     graphql:
 *       clients:
 *         user-service:
 *           address: http://localhost:8080/graphql
 *     rest:
 *       clients:
 *         payment-service:
 *           address: http://localhost:8081
 *     rsocket:
 *       clients:
 *         notification-service:
 *           address: localhost:7000
 * </pre>
 */
@ConfigurationProperties(prefix = "spring.protocol")
public class SpringProtocolProperties {

    private ProtocolConfig grpc = new ProtocolConfig();
    private ProtocolConfig graphql = new ProtocolConfig();
    private ProtocolConfig rest = new ProtocolConfig();
    private ProtocolConfig rsocket = new ProtocolConfig();

    public ProtocolConfig getGrpc() {
        return grpc;
    }

    public void setGrpc(ProtocolConfig grpc) {
        this.grpc = grpc;
    }

    public ProtocolConfig getGraphql() {
        return graphql;
    }

    public void setGraphql(ProtocolConfig graphql) {
        this.graphql = graphql;
    }

    public ProtocolConfig getRest() {
        return rest;
    }

    public void setRest(ProtocolConfig rest) {
        this.rest = rest;
    }

    public ProtocolConfig getRsocket() {
        return rsocket;
    }

    public void setRsocket(ProtocolConfig rsocket) {
        this.rsocket = rsocket;
    }

    public static class ProtocolConfig {

        private Map<String, ServiceConfig> clients = new HashMap<>();

        public Map<String, ServiceConfig> getClients() {
            return clients;
        }

        public void setClients(Map<String, ServiceConfig> clients) {
            this.clients = clients;
        }
    }

    public static class ServiceConfig {

        private String address;
        private String url;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
