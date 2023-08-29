package org.chike.rpc.core.transport.netty.client;

import io.netty.channel.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.chike.rpc.core.domain.Message;
import org.chike.rpc.core.domain.content.RpcResponse;
import org.chike.rpc.core.enums.MessageType;
import org.chike.rpc.core.enums.RpcConfigEnum;
import org.chike.rpc.core.extensions.Compresser;
import org.chike.rpc.core.extensions.Serializer;
import org.chike.rpc.core.factory.ExtensionLoader;
import org.chike.rpc.core.factory.SingletonFactory;

import java.net.InetSocketAddress;

@Slf4j
@ChannelHandler.Sharable
public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    private final NettyClient client = SingletonFactory.getInstance(NettyClient.class);

    private final Serializer serializer = ExtensionLoader
            .getExtensionFromConfig(Serializer.class, RpcConfigEnum.SERIALIZER);

    private final Compresser compresser  = ExtensionLoader
            .getExtensionFromConfig(Compresser.class, RpcConfigEnum.COMPRESSER);


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            log.info("client receive msg: [{}]", msg);
            if (msg instanceof Message) {
                Message respMsg = (Message) msg;
                switch (respMsg.getMessageType()) {
                    case PONG: {
                        log.info("client receive from server: " + MessageType.PONG.getDesc());
                        break;
                    }
                    case RPC_RESP: {
                        client.complete((RpcResponse) respMsg.getContent());
                        break;
                    }
                }
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                log.info("write idle happen [{}]", ctx.channel().remoteAddress());
                Channel channel = client.getActiveChannel((InetSocketAddress) ctx.channel().remoteAddress());
                Message message = Message.builder()
                        .serializer(serializer)
                        .compresser(compresser)
                        .messageType(MessageType.PING)
                        .build();
                channel.writeAndFlush(message).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("client catch exception：", cause);
        cause.printStackTrace();
        ctx.close();
    }
}
