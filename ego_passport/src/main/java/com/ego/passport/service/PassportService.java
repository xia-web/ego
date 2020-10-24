package com.ego.passport.service;

import com.ego.commons.pojo.EgoResult;
import com.ego.pojo.TbUser;

public interface PassportService {
    /**
     * 检查用户是否存在
     * @param tbUser
     * @return
     */
    EgoResult checkUser(TbUser tbUser);

    EgoResult registerUser(TbUser tbUser);
}
