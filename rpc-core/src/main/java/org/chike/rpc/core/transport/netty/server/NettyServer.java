package org.chike.rpc.core.transport.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.NettyRuntime;
import lombok.extern.slf4j.Slf4j;
import org.chike.rpc.core.codec.netty.NettyMessageDecoder;
import org.chike.rpc.core.codec.netty.NettyMessageEncoder;
import org.chike.rpc.core.constant.NettyConstants;
import org.chike.rpc.core.factory.SingletonFactory;
import org.chike.rpc.core.utils.ZkUtil;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

@Slf4j
public class NettyServer {
    public NettyServer() {
        // 清理注册服务的钩子
        clearAllHook();

        NioEventLoopGroup accepters = new NioEventLoopGroup(Math.max(1, NettyRuntime.availableProcessors()));
        NioEventLoopGroup preWorkers = new NioEventLoopGroup();
        NioEventLoopGroup coreWorkers  = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap()
                    .group(accepters, preWorkers)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new IdleStateHandler(30, 0, 0));
                            pipeline.addLast(SingletonFactory.getInstance(NettyMessageEncoder.class));
                            pipeline.addLast(new NettyMessageDecoder());
                            pipeline.addLast(coreWorkers, SingletonFactory.getInstance(NettyServerHandler.class));
                        }
                    });

            String host = InetAddress.getLocalHost().getHostAddress();
            // 绑定端口，同步等待绑定成功
            ChannelFuture channelFuture = serverBootstrap.bind(host, NettyConstants.SERVER_PORT);
            channelFuture.sync();

            log.info("server start!");
            channelFuture.channel().closeFuture().sync();
        } catch (UnknownHostException | InterruptedException e) {
            log.error("occur exception when bootstrap server:", e);
        } finally {
            accepters.shutdownGracefully();
            preWorkers.shutdownGracefully();
            coreWorkers.shutdownGracefully();
        }
    }

    private void clearAllHook() {
        log.info("addShutdownHook for clearAll");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                InetSocketAddress inetSocketAddress = new InetSocketAddress(
                        InetAddress.getLocalHost().getHostAddress(),
                        NettyConstants.SERVER_PORT
                );
                ZkUtil.clearRegistry(inetSocketAddress);
            } catch (UnknownHostException e) {
                log.error(e.getMessage());
            }
        }));
    }
}
