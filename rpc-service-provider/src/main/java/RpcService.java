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

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RpcService {

    /**
     * 用于存储解析出来的Class全路径
     */
    private List<String> classCacheList = new ArrayList<>();

    /**
     * 用于存储Class全路径 -> 对应Class类的实例映射关系
     */
    private Map<String, Object> registryMap = new HashMap<>();

    /**
     * 进行服务发布
     * 
     * @param providerPackage
     */
    public void publish(String providerPackage) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        parseProviderPackage(providerPackage);
        doRegister();
    }

    /**
     * 具体的解析方法，用于获取class的全路径
     * 
     * @param providerPackage
     */
    private void parseProviderPackage(String providerPackage) {
        URL resource = this.getClass().getClassLoader().getResource(providerPackage.replace(".", "/"));

        File dir = new File(resource.getFile());
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                parseProviderPackage(providerPackage + "." + file.getName());
            } else if (file.getName().endsWith(".class")){
                String fileName = file.getName().replace(".class", "").trim();
                classCacheList.add(providerPackage + "." + fileName);
            }
        }
    }

    /**
     * 服务注册，接口名 -> 实例 映射
     */
    public void doRegister() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        if (classCacheList.isEmpty()) {
            return;
        }

        for (String className : classCacheList) {
            Class<?> aClass = Class.forName(className);
            String interfaceName = aClass.getInterfaces()[0].getName();
            registryMap.put(interfaceName, aClass.newInstance());
        }
    }

    /**
     * 启动服务，使用netty启动服务
     */
    public void start() throws InterruptedException {
        EventLoopGroup parentGroup = new NioEventLoopGroup();
        EventLoopGroup childGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(parentGroup, childGroup)
                    // 该参数用于当服务端请求堆积时，先将请求放入队列中，队列长度为1024
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    // 该参数用于表示childChannel 开启心跳检测机制
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new ObjectEncoder());
                            pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                            pipeline.addLast(new MyServerHandler(registryMap));
                        }
                    });

            ChannelFuture channelFuture = serverBootstrap.bind(8888).sync();
            System.out.println("Server Started ......");

            channelFuture.channel().closeFuture().sync();
        }finally {
            parentGroup.shutdownGracefully();
            childGroup.shutdownGracefully();
        }
    }
}
