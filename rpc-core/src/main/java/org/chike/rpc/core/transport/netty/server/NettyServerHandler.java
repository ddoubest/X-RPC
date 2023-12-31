package org.chike.rpc.core.transport.netty.server;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.chike.rpc.core.domain.Message;
import org.chike.rpc.core.domain.ServiceProvider;
import org.chike.rpc.core.domain.content.RpcRequest;
import org.chike.rpc.core.domain.content.RpcResponse;
import org.chike.rpc.core.enums.MessageType;
import org.chike.rpc.core.enums.RpcResponseMessageEnum;
import org.chike.rpc.core.exceptions.RpcRuntimeException;
import org.chike.rpc.core.factory.SingletonFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;


@Slf4j
@ChannelHandler.Sharable
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    private final ServiceProvider serviceProvider = SingletonFactory.getInstance(ServiceProvider.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object sourceMsg) throws Exception {
        try {
            if (sourceMsg instanceof Message) {
                Message msg = (Message) sourceMsg;

                Message.MessageBuilder respMessageBuilder = Message.builder()
                        .serializer(msg.getSerializer())
                        .compresser(msg.getCompresser());

                switch (msg.getMessageType()) {
                    case PING: {
                        respMessageBuilder
                                .messageType(MessageType.PONG);
                        break;
                    }
                    case RPC_REQ: {
                        respMessageBuilder.messageType(MessageType.RPC_RESP);
                        if (!(msg.getContent() instanceof RpcRequest)) {
                            respMessageBuilder
                                .content(
                                        RpcResponse.fail(RpcResponseMessageEnum
                                                .REQ_MESSAGE_NOT_CONTAIN_RPC_REQUEST_INSTANCE
                                                .getMessage())
                                );
                            break;
                        }
                        RpcRequest rpcRequest = (RpcRequest) msg.getContent();
                        RpcResponse rpcResponse = handleRequest(rpcRequest);

                        // 服务端的异步模式
                        if (rpcResponse.getResult() instanceof CompletableFuture) {
                            CompletableFuture<?> future = (CompletableFuture<?>) rpcResponse.getResult();
                            future.whenCompleteAsync((result, exception) -> {
                                if (exception != null) {
                                    log.error("server catch exception: {}", exception.getMessage());
                                    ctx.close();
                                    return;
                                }
                                rpcResponse.setResult(result);
                                respMessageBuilder.content(rpcResponse);
                                Message resp = respMessageBuilder.build();
                                ctx.writeAndFlush(resp).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                            });
                            return;
                        }

                        respMessageBuilder.content(rpcResponse);
                        break;
                    }
                }

                Message resp = respMessageBuilder.build();
                ctx.writeAndFlush(resp).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } finally {
            // Ensure that ByteBuf is released, otherwise there may be memory leaks
            ReferenceCountUtil.release(sourceMsg);
        }
    }

    private RpcResponse handleRequest(RpcRequest rpcRequest) {
        Object provider = serviceProvider.getProvider(rpcRequest.getRpcServiceName());
        if (provider == null) {
            return RpcResponse.fail(RpcResponseMessageEnum.REQUEST_PROVIDER_NOT_EXIST.getMessage());
        }
        try {
            Method method = provider.getClass()
                    .getMethod(rpcRequest.getMethodName(), rpcRequest.getArgsClass());
            Object result = method.invoke(provider, rpcRequest.getArgsInstance());
            return RpcResponse.success(result, rpcRequest.getRequestId());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RpcRuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                log.info("idle check happen, so close the connection");
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("server catch exception: {}", cause.getMessage());
        ctx.close();
    }
}
