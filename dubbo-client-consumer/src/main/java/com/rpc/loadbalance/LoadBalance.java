package com.rpc.loadbalance;

import java.util.List;

/**
 * 定义负载均衡策略接口
 */
public interface LoadBalance {

    String selectService(List<String> serviceList) throws Exception;
}
