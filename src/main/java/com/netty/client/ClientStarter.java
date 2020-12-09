package com.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

public class ClientStarter {

    public static void main(String[] args) throws InterruptedException {

        EventLoopGroup clientGroup = new NioEventLoopGroup(5);
        Bootstrap bootstrap = new Bootstrap();

        try {
            bootstrap.group(clientGroup).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
                    pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
                    // 自定义处理器
                    pipeline.addLast(new MyClientHandler());
                }
            });

            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8888).sync();
            System.out.println("client connected server 8888");
            channelFuture.channel().closeFuture().sync();
        } finally {
            clientGroup.shutdownGracefully();
        }
    }
}
