package com.rpc.discovery;

/**
 * 服务发现接口，用于和ZKServer进行交互
 */
public interface ServiceDiscovery {

    String discovery(String interfaceName) throws Exception;
}
