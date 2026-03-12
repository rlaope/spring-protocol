package io.springprotocol.grpc.core.stub;

public class AsyncStubCreator extends AbstractGrpcStubCreator {

    @Override
    protected String stubMethodName() {
        return "newStub";
    }
}
