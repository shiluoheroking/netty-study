package com.rpc.loadbalance.impl;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.collections4.CollectionUtils;

import com.rpc.loadbalance.LoadBalance;

/**
 * 随机策略
 */
public class RandomLoadBalance implements LoadBalance {
    @Override
    public String selectService(List<String> serviceList) throws Exception {
        if (CollectionUtils.isEmpty(serviceList)) {
            throw new Exception("no service list can use");
        }

        return serviceList.get(ThreadLocalRandom.current().nextInt(serviceList.size()));
    }
}
