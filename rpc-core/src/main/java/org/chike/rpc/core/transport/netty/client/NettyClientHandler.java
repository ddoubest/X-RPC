package org.chike.rpc.core.transport.netty.client;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.chike.rpc.core.domain.Message;
import org.chike.rpc.core.domain.content.RpcResponse;
import org.chike.rpc.core.enums.MessageType;
import org.chike.rpc.core.factory.SingletonFactory;

@Slf4j
@ChannelHandler.Sharable
public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    private final NettyClient client = SingletonFactory.getInstance(NettyClient.class);

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
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("client catch exception：", cause);
        cause.printStackTrace();
        ctx.close();
    }
}
