package com.rpc.provider;

import com.rpc.register.RegisterService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.util.internal.StringUtil;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PublishService {

    /**
     * 缓存解析出来要发布的服务类
     */
    private List<String> classCache = Collections.synchronizedList(new ArrayList<>());

    /**
     * 提供的服务列表
     */
    private Map<String, Object> providerMap = new ConcurrentHashMap<>();

    /**
     * 发布服务的地址
     */
    private String serviceAddress;

    public void publishService(RegisterService registerService, String serviceAddress, String packagePath) throws Exception {
        this.serviceAddress = serviceAddress;
        parseService(packagePath);
        doRegister(registerService, serviceAddress);
    }

    private void parseService(String packagePath) {
        if (StringUtil.isNullOrEmpty(packagePath)) {
            return;
        }

        String replace = packagePath.replace(".", "/");
        URL resource = this.getClass().getClassLoader().getResource(replace);
        File dir = new File(resource.getFile());
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                parseService(packagePath + "." + file.getName());
            } else if (file.getName().endsWith(".class")) {
                String fileName = file.getName().replace(".class", "").trim();
                classCache.add(packagePath + "." + fileName);
            }
        }
    }

    private void doRegister(RegisterService registerService, String serviceAddress) throws Exception{
        if (classCache.isEmpty()) {
            return;
        }
        for (String className : classCache) {
            Class<?> clazz = Class.forName(className);
            String interfaceName = clazz.getInterfaces()[0].getName();
            providerMap.put(className, clazz.newInstance());

            String childNodeName = serviceAddress + ":" + className;
            registerService.register(interfaceName, childNodeName);
        }
    }

    public void start() throws InterruptedException {
        EventLoopGroup parentGroup = new NioEventLoopGroup();
        EventLoopGroup childGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        try {
            serverBootstrap.group(parentGroup, childGroup)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new ObjectEncoder());
                            pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                            pipeline.addLast(new MyServiceHandler(providerMap));
                        }
                    });

            String ip = serviceAddress.split(":")[0];
            String port = serviceAddress.split(":")[1];

            ChannelFuture channelFuture = serverBootstrap.bind(ip, Integer.parseInt(port)).sync();
            System.out.println("Server Provider Has Started ...");
            channelFuture.channel().closeFuture().sync();
        }finally {
            parentGroup.shutdownGracefully();
            childGroup.shutdownGracefully();
        }
    }
}
