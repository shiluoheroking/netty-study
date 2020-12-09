import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class MyServerStarter {

    public static void main(String[] args) throws InterruptedException {

        // parentGroup 用于接收客户端连接，即用于建立连接的
        EventLoopGroup parentGroup = new NioEventLoopGroup(1);
        // childGroup 用于接收客户端读写请求的
        EventLoopGroup childGroup = new NioEventLoopGroup(10);

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        try {

            serverBootstrap.group(parentGroup, childGroup)
                    // 表示服务端处理的请求都是Nio异步的
                    .channel(NioServerSocketChannel.class)
                    // 表示当客户端有新的channel连接来了以后该如何处理
                    .childHandler(new HttpChannelInitializer());

            // 服务端绑定端口，由于bind()是异步操作，所以调用sync()让端口绑定变成同步，从而使得端口可以成功被绑定
            ChannelFuture channelFuture = serverBootstrap.bind(8888).sync();

            System.out.println("netty 服务端启动成功并绑定 8888端口。。。");

            // 当channel被调用close()时，closeFuture() 方法会被调用，同样为了让finally里面的资源顺利关闭，所以需要先将channel进行同步关闭成功
            channelFuture.channel().closeFuture().sync();
        } finally {

            // 平滑的进行资源关闭
            parentGroup.shutdownGracefully();
            childGroup.shutdownGracefully();

        }
    }
}
