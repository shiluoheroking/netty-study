package com.rpc.model;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class UserInfo {
    private static Map<Long, UserInfo> userInfoMap;

    static {
        userInfoMap = new HashMap<Long, UserInfo>();
        userInfoMap.put(5090057989L, new UserInfo(5090057989L, "z小赵"));
        userInfoMap.put(1L, new UserInfo(1L, "GSX250R-A"));
    }

    public static UserInfo getUserInfoByUids(long uid) {
        return userInfoMap.get(uid);
    }

    private long uid;
    private String userName;

    @Override
    public String toString() {
        return "UserInfo{" + "uid=" + uid + ", userName='" + userName + '\'' + '}';
    }
}
