package com.ego.passport.service.impl;

import com.ego.commons.pojo.CartPojo;
import com.ego.commons.pojo.EgoResult;
import com.ego.commons.utils.CookieUtils;
import com.ego.commons.utils.IDUtils;
import com.ego.commons.utils.JsonUtils;
import com.ego.commons.utils.ServletUtil;
import com.ego.dubbo.service.TbUserDubboService;
import com.ego.passport.service.PassportService;
import com.ego.pojo.TbUser;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class PassportServiceImpl implements PassportService {

    // tempcart
    @Value("${ego.cart.tempcart}")
    private String tempCartCookeName;
    // cart:
    @Value("${ego.cart.rediskey}")
    private String cartRedisKey;

    @Reference
    private TbUserDubboService tbUserDubboService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

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
            String cookieValue = CookieUtils.getCookieValueBase64(ServletUtil.getRequest(), tempCartCookeName);

            if (Strings.isNotEmpty(cookieValue)) {
                Map<Long, CartPojo> tempCart = JsonUtils.jsonToMap(cookieValue, Long.class, CartPojo.class);
                String key = cartRedisKey + user.getId();
                List<CartPojo> list = (List<CartPojo>)redisTemplate.opsForValue().get(key);
                for (Long id : tempCart.keySet()) {
                    boolean isExists = false;
                    for (CartPojo c : list) {
                        if (c.getId().equals(id)) {
                            c.setNum(c.getNum() + tempCart.get(id).getNum());
                            isExists = true;
                            break;
                        }
                    }
                    if (!isExists) {
                        list.add(tempCart.get(id));
                    }
                }
                // 合并成功
                redisTemplate.opsForValue().set(key,list);
                // 删除临时会话
                CookieUtils.deleteCookie(ServletUtil.getRequest(),ServletUtil.getResponse(),tempCartCookeName);
            }


            return EgoResult.ok(user);
        }
        return EgoResult.error("用户名或密码不正确");
    }
}
