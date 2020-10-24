package com.ego.dubbo.service.impl;

import com.ego.dubbo.service.TbUserDubboService;
import com.ego.mapper.TbUserMapper;
import com.ego.pojo.TbUser;
import com.ego.pojo.TbUserExample;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class TbUserDubboServiceImpl implements TbUserDubboService {
    @Autowired
    private TbUserMapper tbUserMapper;

    @Override
    public TbUser selectByUser(TbUser user) {
        TbUserExample tbUserExample = new TbUserExample();
        TbUserExample.Criteria criteria = tbUserExample.createCriteria();

        System.out.println(user.getUsername()+"--"+user.getEmail()+user.getPhone());
        // 更具用户名，邮箱，密码进行查询
        if (user.getUsername() != null){
            criteria.andUsernameEqualTo(user.getUsername());
        }else if(user.getEmail() != null){
            criteria.andEmailEqualTo(user.getEmail());
        }else if (user.getPhone() != null){
            criteria.andPhoneEqualTo(user.getPhone());
        }

        List<TbUser> tbUsers = tbUserMapper.selectByExample(tbUserExample);
        if (tbUsers != null && tbUsers.size() >0){
            System.out.println(tbUsers.get(0).getUsername()+tbUsers.get(0).getPhone()+tbUsers.get(0).getEmail());
            return tbUsers.get(0);
        }
        return null;
    }

    @Override
    public int registry(TbUser user) {

        return tbUserMapper.insert(user);
    }
}
