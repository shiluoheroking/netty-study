package com.netty.talk.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class TalkServerHandler extends ChannelInboundHandlerAdapter {

    // GlobalEventExecutor 是一个全局的、单线程安全的
    // ChannelGroup 是一个存放全局所有活跃的channel对象
    private static ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel channel = ctx.channel();
        group.forEach(ch -> {
            // 当前channel是自己，将消息写给自己
            if (ch == channel) {
                channel.writeAndFlush("me: " + msg + "\n");
            } else {
                // 将消息push给当前所有在线的人
                ch.writeAndFlush(channel.remoteAddress() + ":" + msg + "\n");
            }
        });
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        System.out.println(channel.remoteAddress() + "上线了");
        group.writeAndFlush(channel.remoteAddress() + "上线了\n");
        group.add(channel);
    }

    /**
     * 当channel下线时调用该方法
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        group.writeAndFlush(channel.remoteAddress() + "下线了，当前在线人数：" + group.size() + "\n");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
