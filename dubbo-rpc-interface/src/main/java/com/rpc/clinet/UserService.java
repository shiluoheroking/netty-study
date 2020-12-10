package com.rpc.clinet;

import com.rpc.model.UserInfo;

/**
 * 服务接口类，该接口有两个实现类，consumer端通过负载均衡算法随机挑选一个服务进行访问
 *
 * @author z小赵
 */
public interface UserService {
    /**
     * 根据uid获取UserInfo
     *
     * @param uid
     * @return
     */
    UserInfo getUserInfoByUid(long uid);

    /**
     * 根据uid获取Username
     *
     * @param uid
     * @return
     */
    String getUserNameByUid(long uid);
}
