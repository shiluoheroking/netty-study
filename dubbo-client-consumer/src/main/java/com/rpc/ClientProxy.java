package com.rpc;

import com.rpc.consumer.MyClientHandler1;
import com.rpc.discovery.ServiceDiscovery;
import com.rpc.discovery.impl.ServiceDiscoveryImpl;
import com.rpc.model.Invocation;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ClientProxy {

    public static <T> T createProcy(Class<?> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] {clazz}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (Object.class.equals(method.getDeclaringClass())) {
                    return method.invoke(this, args);
                }

                return doInvoke(clazz, method, args);
            }
        });
    }

    private static Object doInvoke(Class<?> clazz, Method method, Object[] args) throws Exception {

        MyClientHandler1 handler = new MyClientHandler1();

        ServiceDiscovery serviceDiscovery = new ServiceDiscoveryImpl();
        String serviceAddress = serviceDiscovery.discovery(clazz.getName());
        if (serviceAddress == null) {
            return null;
        }

        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                            pipeline.addLast(new ObjectEncoder());
                            pipeline.addLast(handler);
                        }
                    });

            String ip = serviceAddress.split(":")[0];
            String port = serviceAddress.split(":")[1];
            String className = serviceAddress.split(":")[2];

            ChannelFuture future = bootstrap.connect(ip, Integer.parseInt(port)).sync();
            Invocation invocation = new Invocation(className, method.getName(), method.getParameterTypes(), args);

            future.channel().writeAndFlush(invocation).sync();

            // 客户端需要等待服务端返回消息，如果不sleep的话，结果还没有返回就结束了，所以会导致客户端拿不到结果
            Thread.sleep(3000);
            future.channel().closeFuture();

        }finally {
            eventLoopGroup.shutdownGracefully();
        }

        return handler.getMsg();
    }
}
