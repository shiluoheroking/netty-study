package com.rpc.register;

import com.rpc.common.ZKConstant;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

public class RegisterServiceImpl implements RegisterService {

    private CuratorFramework curatorFramework;

    /**
     * 使用curator 建立zk客户端和zk服务端的通信
     */
    {
        curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(ZKConstant.ZK_CLUSTER)
                .sessionTimeoutMs(5000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 10))
                .build();
        curatorFramework.start();
    }

    @Override
    public void register(String registerName, String serviceAddress) throws Exception {
        String servicePath = ZKConstant.ZK_SERVICE_ROOT_PATH + "/" + registerName;
        if (curatorFramework.checkExists().forPath(servicePath) == null) {
            curatorFramework.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT)
                    .forPath(servicePath);
        }

        String addressPath = servicePath + "/" + serviceAddress;
        if (curatorFramework.checkExists().forPath(addressPath) == null) {
            curatorFramework.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL)
                    .forPath(addressPath);
        }
    }
}
