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
        if (tbUser1 == null) {
            return EgoResult.ok();
        }
        return EgoResult.error("用户名重复");
    }

    @Override
    public EgoResult registerUser(TbUser tbUser) {
        Date date = new Date();
        tbUser.setId(IDUtils.genItemId());
        tbUser.setUpdated(date);
        tbUser.setCreated(date);
        String pwd = DigestUtils.md5DigestAsHex(tbUser.getPassword().getBytes());
        tbUser.setPassword(pwd);
        int index = tbUserDubboService.registry(tbUser);

        if (index == 1) {
            return EgoResult.ok();
        }
        return EgoResult.error("注册失败");
    }

    @Override
    public EgoResult login(TbUser tbUser) {
        // 需要对密码进行加密
        String pwdMd = DigestUtils.md5DigestAsHex(tbUser.getPassword().getBytes());
        tbUser.setPassword(pwdMd);
        TbUser user = tbUserDubboService.selectByUsernamePwd(tbUser);
        if (user != null) { // 一定要把 user 放在 Egoresult 中，控制器需要把用户信息放到作用域
            return EgoResult.ok(user);
        }
        return EgoResult.error("用户名或密码不正确");
    }
}
