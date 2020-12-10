public class ProviderStarter {

    public static void main(String[] args) throws IllegalAccessException, ClassNotFoundException, InstantiationException, InterruptedException {
        RpcService rpcService = new RpcService();
        // 1. 发布服务（注意：此处传递的包名为服务具体实现类的类路径）
        rpcService.publish("com.rpc.provider");
        // 2. 启动服务
        rpcService.start();
    }
}
