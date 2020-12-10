import com.rpc.ClientProxy;
import com.rpc.clinet.UserService;

public class ConsumerStarter {

    public static void main(String[] args) throws Exception {

        UserService userService = ClientProxy.createProcy(UserService.class);

        String userNameByUid = userService.getUserNameByUid(5090057989L);

        System.out.println(userNameByUid);
    }
}
