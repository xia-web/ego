package com.ego.passport.service.impl;

import com.ego.commons.pojo.EgoResult;
import com.ego.commons.utils.IDUtils;
import com.ego.dubbo.service.TbUserDubboService;
import com.ego.passport.service.PassportService;
import com.ego.pojo.TbUser;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Date;

@Service
public class PassportServiceImpl implements PassportService {

    @Reference
    private TbUserDubboService tbUserDubboService;

    @Override
    public EgoResult checkUser(TbUser tbUser) {
        TbUser tbUser1 = tbUserDubboService.selectByUser(tbUser);
        if (tbUser1  == null){
            return EgoResult.ok();
        }
        return EgoResult.error("用户名重复");
    }

    @Override
    public EgoResult registerUser(TbUser tbUser) {
        Date date =new Date();
        tbUser.setId(IDUtils.genItemId());
        tbUser.setUpdated(date);
        tbUser.setCreated(date);
        String pwd = DigestUtils.md5DigestAsHex(tbUser.getPassword().getBytes());
        tbUser.setPassword(pwd);
        int index = tbUserDubboService.registry(tbUser);

        if (index == 1){
            return EgoResult.ok();
        }
        return EgoResult.error("注册失败");
    }
}
