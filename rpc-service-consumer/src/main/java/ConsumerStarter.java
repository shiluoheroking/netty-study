import com.api.common.ProviderService;

/**
 * 服务消费类
 */
public class ConsumerStarter {
    public static void main(String[] args) {
        RpcProxy rpcProxy = new RpcProxy();
        ProviderService providerService = rpcProxy.create(ProviderService.class);
        System.out.println(providerService.getUserNameByUid(5090057989L));
    }
}
