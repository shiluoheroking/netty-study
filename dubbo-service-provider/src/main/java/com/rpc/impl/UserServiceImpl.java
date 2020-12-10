package com.rpc.impl;

import com.rpc.clinet.UserService;
import com.rpc.model.UserInfo;

/**
 * 默认实现
 */
public class UserServiceImpl implements UserService {
    @Override
    public UserInfo getUserInfoByUid(long uid) {
        return UserInfo.getUserInfoByUids(uid);
    }

    @Override
    public String getUserNameByUid(long uid) {
        String result = "用户不存在";
        UserInfo userInfo = UserInfo.getUserInfoByUids(uid);
        if (userInfo != null) {
            result = userInfo.getUserName();
        }
        return result;
    }
}
