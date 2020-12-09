package com.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

public class ServerStarter {

    public static void main(String[] args) throws InterruptedException {

        EventLoopGroup parentGroup = new NioEventLoopGroup(1);
        EventLoopGroup childGroup = new NioEventLoopGroup(10);

        ServerBootstrap serverBootstrap = new ServerBootstrap();

        try {

            serverBootstrap.group(parentGroup, childGroup).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    ChannelPipeline pipeline = socketChannel.pipeline();
                    // 设置编解码
                    pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
                    pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
                    // 设置自定义处理器
                    pipeline.addLast(new MyServerHandler());
                }
            });

            // 绑定端口
            ChannelFuture channelFuture = serverBootstrap.bind(8888).sync();
            System.out.println("server started and bind port 8888");

            // 当channel被调用close() 方法时，会调用closeFuture 来关闭channel
            channelFuture.channel().closeFuture().sync();

        } finally {
            parentGroup.shutdownGracefully();
            childGroup.shutdownGracefully();
        }
    }
}
