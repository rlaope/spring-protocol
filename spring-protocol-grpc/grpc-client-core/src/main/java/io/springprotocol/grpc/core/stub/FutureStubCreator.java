package io.springprotocol.grpc.core.stub;

public class FutureStubCreator extends AbstractGrpcStubCreator {

    @Override
    protected String stubMethodName() {
        return "newFutureStub";
    }
}
