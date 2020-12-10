package com.rpc.discovery.impl;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.retry.ExponentialBackoffRetry;

import com.rpc.common.ZKConstant;
import com.rpc.discovery.ServiceDiscovery;
import com.rpc.loadbalance.impl.RandomLoadBalance;

public class ServiceDiscoveryImpl implements ServiceDiscovery {
    private CuratorFramework curator;
    private List<String> serviceList;

    public ServiceDiscoveryImpl() {
        this.curator = CuratorFrameworkFactory.builder()
                .connectString(ZKConstant.ZK_CLUSTER)
                .sessionTimeoutMs(5000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 10))
                .build();
        curator.start();
    }

    @Override
    public String discovery(String interfaceName) throws Exception {
        String path = ZKConstant.ZK_SERVICE_ROOT_PATH + "/" + interfaceName;
        serviceList = curator.getChildren().forPath(path);

        // 注册watcher，持续监听ZKServer中提供的服务列表的变化
        registerWatch(path);

        if (CollectionUtils.isEmpty(serviceList)) {
            return null;
        }

        // 服务列表中只有一个服务，则直接返回
        if (serviceList.size() == 1) {
            return serviceList.get(0);
        }

        // 随机返回一个服务
        return new RandomLoadBalance().selectService(serviceList);
    }

    /**
     * 监听ZKServer服务列表的变化
     *
     * @param servicePath 监听的服务地址
     * @throws Exception
     */
    private void registerWatch(String servicePath) throws Exception {
        PathChildrenCache cache = new PathChildrenCache(curator, servicePath, true);
        cache.getListenable().addListener((client, event) -> serviceList = curator.getChildren().forPath(servicePath));
        cache.start();
    }
}
