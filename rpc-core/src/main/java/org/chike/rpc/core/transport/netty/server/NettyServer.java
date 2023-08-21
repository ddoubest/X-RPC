package org.chike.rpc.core.transport.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.NettyRuntime;
import org.chike.rpc.core.codec.netty.NettyMessageDecoder;
import org.chike.rpc.core.codec.netty.NettyMessageEncoder;
import org.chike.rpc.core.factory.SingletonFactory;


public class NettyServer {

    NettyServer() {
        NioEventLoopGroup accepters = new NioEventLoopGroup(Math.max(1, NettyRuntime.availableProcessors()));
        NioEventLoopGroup preWorkers = new NioEventLoopGroup();
        NioEventLoopGroup coreWorkers = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap()
                .group(accepters, preWorkers)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(SingletonFactory.getInstance(NettyMessageEncoder.class));
                        pipeline.addLast(SingletonFactory.getInstance(NettyMessageDecoder.class));
                    }
                });

    }
}
