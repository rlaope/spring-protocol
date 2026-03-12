package io.springprotocol.grpc.spring.factory;

import io.springprotocol.grpc.core.proxy.GrpcClientProxyFactory;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

/**
 * Spring FactoryBean that produces a gRPC client proxy for a given interface.
 * Resolves the target address from the Environment using the serviceId.
 */
public class GrpcClientFactoryBean<T> implements FactoryBean<T> {

    private Class<T> interfaceType;
    private Class<?> grpcClass;
    private String serviceId;

    @Autowired
    private GrpcClientProxyFactory proxyFactory;

    @Autowired
    private Environment environment;

    @Override
    @SuppressWarnings("unchecked")
    public T getObject() {
        String address = resolveAddress();
        return proxyFactory.createProxy(interfaceType, grpcClass, address);
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceType;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    private String resolveAddress() {
        String propertyKey = "grpc.client." + serviceId + ".address";
        String address = environment.getProperty(propertyKey);
        if (address == null || address.isBlank()) {
            throw new IllegalStateException(
                    "No gRPC address configured for service '" + serviceId
                            + "'. Set '" + propertyKey + "' in your configuration.");
        }
        return address;
    }

    public void setInterfaceType(Class<T> interfaceType) {
        this.interfaceType = interfaceType;
    }

    public void setGrpcClass(Class<?> grpcClass) {
        this.grpcClass = grpcClass;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }
}
