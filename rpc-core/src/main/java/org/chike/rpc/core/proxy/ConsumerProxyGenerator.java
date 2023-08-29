package org.chike.rpc.core.proxy;

import lombok.extern.slf4j.Slf4j;
import org.chike.rpc.core.constant.RpcConstants;
import org.chike.rpc.core.domain.content.RpcRequest;
import org.chike.rpc.core.domain.content.RpcResponse;
import org.chike.rpc.core.enums.RpcResponseCodeEnum;
import org.chike.rpc.core.enums.RpcRuntimeErrorMessageEnum;
import org.chike.rpc.core.exceptions.RpcRuntimeException;
import org.chike.rpc.core.factory.SingletonFactory;
import org.chike.rpc.core.transport.netty.client.NettyClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ConsumerProxyGenerator implements InvocationHandler {
    private final NettyClient client = SingletonFactory.getInstance(NettyClient.class);

    private final String group;
    private final String version;

    public ConsumerProxyGenerator(String group, String version) {
        this.group = group;
        this.version = version;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest rpcRequest = RpcRequest.builder()
                .requestId(UUID.randomUUID().toString())
                .interfaceName(method.getDeclaringClass().getCanonicalName())
                .methodName(method.getName())
                .argsClass(method.getParameterTypes())
                .argsInstance(args)
                .group(this.group)
                .version(this.version)
                .build();

        log.info("rpc call invoke: {}", rpcRequest.toString());

        CompletableFuture<RpcResponse> responseFuture = client.sendRpcRequest(rpcRequest);

        // 异步模式适配
        if (method.getReturnType() == CompletableFuture.class) {
            return CompletableFuture.supplyAsync(() -> {
                RpcResponse rpcResponse = getAndCheckResponse(responseFuture, rpcRequest);
                return rpcResponse.getResult();
            });
        }

        RpcResponse rpcResponse = getAndCheckResponse(responseFuture, rpcRequest);
        return rpcResponse.getResult();
    }

    public <T> T getProxy(Class<T> clazz) {
        return clazz.cast(Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class<?>[]{clazz},
                this));
    }

    private RpcResponse getAndCheckResponse(CompletableFuture<RpcResponse> responseFuture, RpcRequest rpcRequest) {
        RpcResponse rpcResponse;
        try {
            rpcResponse = responseFuture.get(RpcConstants.TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw new RpcRuntimeException(
                    RpcRuntimeErrorMessageEnum.SERVICE_INVOCATION_FAILURE,
                    e.toString());
        }
        check(rpcResponse, rpcRequest);
        return rpcResponse;
    }

    private void check(RpcResponse rpcResponse, RpcRequest rpcRequest) {
        if (rpcResponse == null) {
            throw new RpcRuntimeException(RpcRuntimeErrorMessageEnum.SERVICE_INVOCATION_FAILURE,rpcRequest.getRpcServiceName());
        }

        if (rpcResponse.getResponseCode() == null || !rpcResponse.getResponseCode().equals(RpcResponseCodeEnum.SUCCESS.getCode())) {
            throw new RpcRuntimeException(RpcRuntimeErrorMessageEnum.SERVICE_INVOCATION_FAILURE, rpcRequest.getRpcServiceName());
        }

        if (!rpcRequest.getRequestId().equals(rpcResponse.getRequestId())) {
            throw new RpcRuntimeException(RpcRuntimeErrorMessageEnum.REQUEST_NOT_MATCH_RESPONSE, rpcRequest.getRpcServiceName());
        }
    }
}
