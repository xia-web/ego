package com.ego.dubbo.service;

import com.ego.pojo.TbUser;

public interface TbUserDubboService {
    /**
     * 查询用户
     * @param user
     * @return
     */
    TbUser selectByUser(TbUser user);

    /**
     * 用户注册
     * @param user
     * @return
     */
    int registry(TbUser user);

    /**
     *
     * @param tbUser
     * @return
     */
    TbUser selectByUsernamePwd(TbUser tbUser);
}
