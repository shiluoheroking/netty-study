import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

/**
 * 自定义handler
 */
public class HttpServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 读取channel中的数据，即读取客户端发送的请求
     * 
     * @param ctx 上下文信息
     * @param msg 客户端发送的信息
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("msg: " + msg);

        // 如果请求是httpRequest的话，则对客户端进行响应
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            System.out.println("uri: " + request.getUri());
            System.out.println("method: " + request.getMethod());

            ByteBuf byteBuf = Unpooled.copiedBuffer("Hello Netty Browers", CharsetUtil.UTF_8);

            // 构建response
            DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);

            // 设置消息头
            HttpHeaders headers = response.headers();
            headers.add(HttpHeaderNames.CONTENT_TYPE, "text/plain");
            headers.add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());

            ctx.writeAndFlush(response);
        }
    }

    /**
     * 当在处理channel的过程中发生异常时，会调用该方法
     * 
     * @param ctx 上下文信息
     * @param cause 异常信息
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        // 发生异常时，关闭连接
        ctx.close();
    }
}
