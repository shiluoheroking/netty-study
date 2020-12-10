package com.rpc.provider;

import com.api.common.ProviderService;

/**
 * 服务接口的具体实现类
 */
public class ProviderServiceImpl implements ProviderService {
    @Override
    public String getUserNameByUid(long uid) {
        return uid + " name is z小赵";
    }
}
