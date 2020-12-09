import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

public class HttpChannelInitializer extends ChannelInitializer<SocketChannel> {

    /**
     * 当客户端channel被创建以后，就会调用initChannel() 方法，对channel进行一些初始化操作
     * 这个操作类似于Spring中对实例化的bean实例进行属性的填充操作
     * 
     * @param socketChannel
     * @throws Exception
     */
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        // 通过socketChannel获取到pipeline
        ChannelPipeline pipeline = socketChannel.pipeline();

        // 在pipeline中添加执行链，首先添加一个服务端的http编解码器
        pipeline.addLast(new HttpServerCodec());

        // 添加一个自定义的ChannelHandler
        pipeline.addLast(new HttpServerHandler());
    }
}
