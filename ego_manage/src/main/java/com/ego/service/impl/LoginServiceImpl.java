package com.ego.service.impl;


import com.ego.dubbo.service.ManagerDubboService;
import com.ego.pojo.Manager;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Consumer的注解是Spring的。
 */
@Service
public class LoginServiceImpl implements UserDetailsService {
    // 也是apache注解，如果导入jdk的注解会出现空指针
    @Reference
    private ManagerDubboService managerDubboService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Manager manager = managerDubboService.selectManagerByUsername(username);
        if (manager==null){
            throw new UsernameNotFoundException("用户不存在");
        }

        return new User(username,manager.getPassword(), AuthorityUtils.commaSeparatedStringToAuthorityList("不涉及权限"));
    }
}
