package org.chike.rpc.core.transport.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.chike.rpc.core.codec.netty.NettyMessageDecoder;
import org.chike.rpc.core.codec.netty.NettyMessageEncoder;
import org.chike.rpc.core.domain.content.RpcRequest;
import org.chike.rpc.core.enums.RpcConfigEnum;
import org.chike.rpc.core.extensions.RegistryCenter;
import org.chike.rpc.core.factory.ExtensionLoader;
import org.chike.rpc.core.factory.SingletonFactory;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class NettyClient {
    private Bootstrap client;

    private final RegistryCenter registryCenter = ExtensionLoader
            .getExtensionFromConfig(RegistryCenter.class, RpcConfigEnum.REGISTRY_CENTER_NAME);

    private final Map<String, Channel> connectedChannels = new ConcurrentHashMap<>();

    public NettyClient() {
        NioEventLoopGroup workers = new NioEventLoopGroup();

        shutdownHook(workers);

        this.client = new Bootstrap()
                .group(workers)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(SingletonFactory.getInstance(NettyMessageEncoder.class));
                        pipeline.addLast(SingletonFactory.getInstance(NettyMessageDecoder.class));
                    }
                });
    }

    public Object sendRpcRequest(RpcRequest rpcRequest) {
        InetSocketAddress address = registryCenter.discoverService(rpcRequest);
        Channel channel = getActiveChannel(address);
        return null;
    }

    private Channel getActiveChannel(InetSocketAddress address) {
        Channel channel = connectedChannels.computeIfAbsent(address.toString(), ignored -> connect(address));
        if (!channel.isActive()) {
            Channel activeChannel = connect(address);
            connectedChannels.put(address.toString(), activeChannel);
            return activeChannel;
        }
        return channel;
    }

    @SneakyThrows
    private Channel connect(InetSocketAddress address) {
        CompletableFuture<Channel> result = new CompletableFuture<>();
        client.connect(address).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                log.info("The client has connected [{}] successful!", address.toString());
                result.complete(future.channel());
            } else {
                throw new IllegalStateException();
            }
        });
        return result.get();
    }

    private void shutdownHook(NioEventLoopGroup workers) {
        Runtime.getRuntime().addShutdownHook(new Thread(workers::shutdownGracefully));
    }
}
