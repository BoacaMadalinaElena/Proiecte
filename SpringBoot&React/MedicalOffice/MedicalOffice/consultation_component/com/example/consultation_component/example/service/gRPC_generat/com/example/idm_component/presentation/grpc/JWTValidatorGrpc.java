package com.example.idm_component.presentation.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.41.0)",
    comments = "Source: IDMService.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class JWTValidatorGrpc {

  private JWTValidatorGrpc() {}

  public static final String SERVICE_NAME = "com.example.idm_component.presentation.grpc.JWTValidator";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.example.idm_component.presentation.grpc.JWTValidateRequest,
      com.example.idm_component.presentation.grpc.JWTValidateResponse> getValidateJWTMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "validateJWT",
      requestType = com.example.idm_component.presentation.grpc.JWTValidateRequest.class,
      responseType = com.example.idm_component.presentation.grpc.JWTValidateResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.idm_component.presentation.grpc.JWTValidateRequest,
      com.example.idm_component.presentation.grpc.JWTValidateResponse> getValidateJWTMethod() {
    io.grpc.MethodDescriptor<com.example.idm_component.presentation.grpc.JWTValidateRequest, com.example.idm_component.presentation.grpc.JWTValidateResponse> getValidateJWTMethod;
    if ((getValidateJWTMethod = JWTValidatorGrpc.getValidateJWTMethod) == null) {
      synchronized (JWTValidatorGrpc.class) {
        if ((getValidateJWTMethod = JWTValidatorGrpc.getValidateJWTMethod) == null) {
          JWTValidatorGrpc.getValidateJWTMethod = getValidateJWTMethod =
              io.grpc.MethodDescriptor.<com.example.idm_component.presentation.grpc.JWTValidateRequest, com.example.idm_component.presentation.grpc.JWTValidateResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "validateJWT"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.idm_component.presentation.grpc.JWTValidateRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.idm_component.presentation.grpc.JWTValidateResponse.getDefaultInstance()))
              .setSchemaDescriptor(new JWTValidatorMethodDescriptorSupplier("validateJWT"))
              .build();
        }
      }
    }
    return getValidateJWTMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.idm_component.presentation.grpc.JWTValidateRequest,
      com.example.idm_component.presentation.grpc.EmptyGRPCResponse> getLogoutMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "logout",
      requestType = com.example.idm_component.presentation.grpc.JWTValidateRequest.class,
      responseType = com.example.idm_component.presentation.grpc.EmptyGRPCResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.idm_component.presentation.grpc.JWTValidateRequest,
      com.example.idm_component.presentation.grpc.EmptyGRPCResponse> getLogoutMethod() {
    io.grpc.MethodDescriptor<com.example.idm_component.presentation.grpc.JWTValidateRequest, com.example.idm_component.presentation.grpc.EmptyGRPCResponse> getLogoutMethod;
    if ((getLogoutMethod = JWTValidatorGrpc.getLogoutMethod) == null) {
      synchronized (JWTValidatorGrpc.class) {
        if ((getLogoutMethod = JWTValidatorGrpc.getLogoutMethod) == null) {
          JWTValidatorGrpc.getLogoutMethod = getLogoutMethod =
              io.grpc.MethodDescriptor.<com.example.idm_component.presentation.grpc.JWTValidateRequest, com.example.idm_component.presentation.grpc.EmptyGRPCResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "logout"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.idm_component.presentation.grpc.JWTValidateRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.idm_component.presentation.grpc.EmptyGRPCResponse.getDefaultInstance()))
              .setSchemaDescriptor(new JWTValidatorMethodDescriptorSupplier("logout"))
              .build();
        }
      }
    }
    return getLogoutMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.idm_component.presentation.grpc.CredentialsRequest,
      com.example.idm_component.presentation.grpc.JWTValidateRequest> getLoginMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "login",
      requestType = com.example.idm_component.presentation.grpc.CredentialsRequest.class,
      responseType = com.example.idm_component.presentation.grpc.JWTValidateRequest.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.idm_component.presentation.grpc.CredentialsRequest,
      com.example.idm_component.presentation.grpc.JWTValidateRequest> getLoginMethod() {
    io.grpc.MethodDescriptor<com.example.idm_component.presentation.grpc.CredentialsRequest, com.example.idm_component.presentation.grpc.JWTValidateRequest> getLoginMethod;
    if ((getLoginMethod = JWTValidatorGrpc.getLoginMethod) == null) {
      synchronized (JWTValidatorGrpc.class) {
        if ((getLoginMethod = JWTValidatorGrpc.getLoginMethod) == null) {
          JWTValidatorGrpc.getLoginMethod = getLoginMethod =
              io.grpc.MethodDescriptor.<com.example.idm_component.presentation.grpc.CredentialsRequest, com.example.idm_component.presentation.grpc.JWTValidateRequest>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "login"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.idm_component.presentation.grpc.CredentialsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.idm_component.presentation.grpc.JWTValidateRequest.getDefaultInstance()))
              .setSchemaDescriptor(new JWTValidatorMethodDescriptorSupplier("login"))
              .build();
        }
      }
    }
    return getLoginMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static JWTValidatorStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<JWTValidatorStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<JWTValidatorStub>() {
        @java.lang.Override
        public JWTValidatorStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new JWTValidatorStub(channel, callOptions);
        }
      };
    return JWTValidatorStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static JWTValidatorBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<JWTValidatorBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<JWTValidatorBlockingStub>() {
        @java.lang.Override
        public JWTValidatorBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new JWTValidatorBlockingStub(channel, callOptions);
        }
      };
    return JWTValidatorBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static JWTValidatorFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<JWTValidatorFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<JWTValidatorFutureStub>() {
        @java.lang.Override
        public JWTValidatorFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new JWTValidatorFutureStub(channel, callOptions);
        }
      };
    return JWTValidatorFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class JWTValidatorImplBase implements io.grpc.BindableService {

    /**
     */
    public void validateJWT(com.example.idm_component.presentation.grpc.JWTValidateRequest request,
        io.grpc.stub.StreamObserver<com.example.idm_component.presentation.grpc.JWTValidateResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getValidateJWTMethod(), responseObserver);
    }

    /**
     */
    public void logout(com.example.idm_component.presentation.grpc.JWTValidateRequest request,
        io.grpc.stub.StreamObserver<com.example.idm_component.presentation.grpc.EmptyGRPCResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getLogoutMethod(), responseObserver);
    }

    /**
     */
    public void login(com.example.idm_component.presentation.grpc.CredentialsRequest request,
        io.grpc.stub.StreamObserver<com.example.idm_component.presentation.grpc.JWTValidateRequest> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getLoginMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getValidateJWTMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.example.idm_component.presentation.grpc.JWTValidateRequest,
                com.example.idm_component.presentation.grpc.JWTValidateResponse>(
                  this, METHODID_VALIDATE_JWT)))
          .addMethod(
            getLogoutMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.example.idm_component.presentation.grpc.JWTValidateRequest,
                com.example.idm_component.presentation.grpc.EmptyGRPCResponse>(
                  this, METHODID_LOGOUT)))
          .addMethod(
            getLoginMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.example.idm_component.presentation.grpc.CredentialsRequest,
                com.example.idm_component.presentation.grpc.JWTValidateRequest>(
                  this, METHODID_LOGIN)))
          .build();
    }
  }

  /**
   */
  public static final class JWTValidatorStub extends io.grpc.stub.AbstractAsyncStub<JWTValidatorStub> {
    private JWTValidatorStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected JWTValidatorStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new JWTValidatorStub(channel, callOptions);
    }

    /**
     */
    public void validateJWT(com.example.idm_component.presentation.grpc.JWTValidateRequest request,
        io.grpc.stub.StreamObserver<com.example.idm_component.presentation.grpc.JWTValidateResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getValidateJWTMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void logout(com.example.idm_component.presentation.grpc.JWTValidateRequest request,
        io.grpc.stub.StreamObserver<com.example.idm_component.presentation.grpc.EmptyGRPCResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getLogoutMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void login(com.example.idm_component.presentation.grpc.CredentialsRequest request,
        io.grpc.stub.StreamObserver<com.example.idm_component.presentation.grpc.JWTValidateRequest> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getLoginMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class JWTValidatorBlockingStub extends io.grpc.stub.AbstractBlockingStub<JWTValidatorBlockingStub> {
    private JWTValidatorBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected JWTValidatorBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new JWTValidatorBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.example.idm_component.presentation.grpc.JWTValidateResponse validateJWT(com.example.idm_component.presentation.grpc.JWTValidateRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getValidateJWTMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.example.idm_component.presentation.grpc.EmptyGRPCResponse logout(com.example.idm_component.presentation.grpc.JWTValidateRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getLogoutMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.example.idm_component.presentation.grpc.JWTValidateRequest login(com.example.idm_component.presentation.grpc.CredentialsRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getLoginMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class JWTValidatorFutureStub extends io.grpc.stub.AbstractFutureStub<JWTValidatorFutureStub> {
    private JWTValidatorFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected JWTValidatorFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new JWTValidatorFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.idm_component.presentation.grpc.JWTValidateResponse> validateJWT(
        com.example.idm_component.presentation.grpc.JWTValidateRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getValidateJWTMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.idm_component.presentation.grpc.EmptyGRPCResponse> logout(
        com.example.idm_component.presentation.grpc.JWTValidateRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getLogoutMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.idm_component.presentation.grpc.JWTValidateRequest> login(
        com.example.idm_component.presentation.grpc.CredentialsRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getLoginMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_VALIDATE_JWT = 0;
  private static final int METHODID_LOGOUT = 1;
  private static final int METHODID_LOGIN = 2;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final JWTValidatorImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(JWTValidatorImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_VALIDATE_JWT:
          serviceImpl.validateJWT((com.example.idm_component.presentation.grpc.JWTValidateRequest) request,
              (io.grpc.stub.StreamObserver<com.example.idm_component.presentation.grpc.JWTValidateResponse>) responseObserver);
          break;
        case METHODID_LOGOUT:
          serviceImpl.logout((com.example.idm_component.presentation.grpc.JWTValidateRequest) request,
              (io.grpc.stub.StreamObserver<com.example.idm_component.presentation.grpc.EmptyGRPCResponse>) responseObserver);
          break;
        case METHODID_LOGIN:
          serviceImpl.login((com.example.idm_component.presentation.grpc.CredentialsRequest) request,
              (io.grpc.stub.StreamObserver<com.example.idm_component.presentation.grpc.JWTValidateRequest>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class JWTValidatorBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    JWTValidatorBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.example.idm_component.presentation.grpc.IDMService.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("JWTValidator");
    }
  }

  private static final class JWTValidatorFileDescriptorSupplier
      extends JWTValidatorBaseDescriptorSupplier {
    JWTValidatorFileDescriptorSupplier() {}
  }

  private static final class JWTValidatorMethodDescriptorSupplier
      extends JWTValidatorBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    JWTValidatorMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (JWTValidatorGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new JWTValidatorFileDescriptorSupplier())
              .addMethod(getValidateJWTMethod())
              .addMethod(getLogoutMethod())
              .addMethod(getLoginMethod())
              .build();
        }
      }
    }
    return result;
  }
}
