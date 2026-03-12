package io.springprotocol.grpc.core.stub;

import io.grpc.Channel;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public abstract class AbstractGrpcStubCreator implements GrpcStubCreator {

    protected abstract String stubMethodName();

    @Override
    public Object create(Class<?> grpcClass, Channel channel) {
        try {
            Method method = grpcClass.getMethod(stubMethodName(), Channel.class);
            if (!Modifier.isStatic(method.getModifiers())) {
                throw new StubCreationException(
                        stubMethodName() + " must be a static method on " + grpcClass.getName());
            }
            return method.invoke(null, channel);
        } catch (NoSuchMethodException e) {
            throw new StubCreationException(
                    "No " + stubMethodName() + "(Channel) method found on " + grpcClass.getName(), e);
        } catch (StubCreationException e) {
            throw e;
        } catch (Exception e) {
            throw new StubCreationException(
                    "Failed to create stub via " + stubMethodName() + " for " + grpcClass.getName(), e);
        }
    }
}
