import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Map;

public class MyServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 服务映射
     */
    private Map<String, Object> registryMap;

    public MyServerHandler(Map<String, Object> registryMap) {
        this.registryMap = registryMap;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Object result = "Service Not Provider";
        if (msg instanceof Invocation) {
            Invocation invocation = (Invocation) msg;
            String className = invocation.getClassName();
            if (registryMap.containsKey(className)) {
                Object provider = registryMap.get(className);
                result = provider.getClass()
                                .getMethod(invocation.getMethodName(), invocation.getParamTypes())
                                .invoke(provider, invocation.getParamValues());
            }
        }
        ctx.channel().writeAndFlush(result);
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
