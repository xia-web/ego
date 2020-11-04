package com.ego.cart.interceptor;

import com.ego.pojo.TbUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Value("${ego.passport.loginurl}")
    private String loginUrl;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        TbUser tbUser = (TbUser)request.getSession().getAttribute("loginUser");
        if (tbUser != null){
            return true;
        }
        response.sendRedirect(loginUrl);
        return false;
    }
}
