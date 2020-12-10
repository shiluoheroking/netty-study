package com.rpc.impl;

import com.rpc.clinet.UserService;
import com.rpc.model.UserInfo;

/**
 * v2版本下的实现方式
 */
public class UserServiceV2Impl implements UserService {
    @Override
    public UserInfo getUserInfoByUid(long uid) {
        return UserInfo.getUserInfoByUids(uid);
    }

    @Override
    public String getUserNameByUid(long uid) {
        String result = "v2 下用户不存在";
        UserInfo userInfo = UserInfo.getUserInfoByUids(uid);
        if (userInfo != null) {
            result = "v2 下用户名称为：" + userInfo.getUserName();
        }
        return result;
    }
}
