package io.springprotocol.grpc.core.stub;

public class BlockingStubCreator extends AbstractGrpcStubCreator {

    @Override
    protected String stubMethodName() {
        return "newBlockingStub";
    }
}
