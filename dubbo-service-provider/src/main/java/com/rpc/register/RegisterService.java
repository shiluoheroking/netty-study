package com.rpc.register;

/**
 * 将想要发布的服务注册到zk上
 */
public interface RegisterService {

    /**
     * 将服务注册到zk上
     *
     * @param registerName 注册的服务名称
     * @param serviceAddress 服务提供的地址
     */
    void register(String registerName, String serviceAddress) throws Exception;
}
