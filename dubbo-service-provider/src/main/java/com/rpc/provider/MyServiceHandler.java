package com.rpc.provider;

import com.rpc.model.Invocation;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.lang.reflect.Method;
import java.util.Map;

public class MyServiceHandler extends ChannelInboundHandlerAdapter {
    private Map<String, Object> providerMap;

    public MyServiceHandler(Map<String, Object> providerMap) {
        this.providerMap = providerMap;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Invocation) {
            Object result = "Service Not Provider";
            Invocation invocation = (Invocation) msg;
            Object o = providerMap.get(invocation.getClassName());
            if (o != null) {
                Method method = o.getClass().getMethod(invocation.getMethodName(), invocation.getParameterType());
                result = method.invoke(o, invocation.getParameterValue());
            }
            System.out.println("service return msg: " + result);
            ctx.channel().writeAndFlush(result).sync();
            ctx.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
