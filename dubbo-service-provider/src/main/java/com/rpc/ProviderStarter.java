package com.rpc;

import com.rpc.provider.PublishService;
import com.rpc.register.RegisterService;
import com.rpc.register.RegisterServiceImpl;

public class ProviderStarter {
    public static void main(String[] args) throws Exception {
        RegisterService registerService = new RegisterServiceImpl();
        String serviceAddress = "127.0.0.1:9999";
        // 需要发布的服务所在目录
        String packagePath = "com.rpc.impl";

        PublishService publishService = new PublishService();
        // 解析服务，并将其注册服务到zk上
        publishService.publishService(registerService, serviceAddress, packagePath);

        publishService.start();

    }
}
