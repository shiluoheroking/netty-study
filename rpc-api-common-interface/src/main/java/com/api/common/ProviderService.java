package com.api.common;

/**
 * 服务提供的接口，供服务端实现者和服务消费者进行调用
 */
public interface ProviderService {

    /**
     * genjv
     * @param uid
     * @return
     */
    String getUserNameByUid(long uid);
}
