package com.ego.passport.controller;

import com.ego.commons.pojo.EgoResult;
import com.ego.passport.service.PassportService;
import com.ego.pojo.TbUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
public class PassportController {
    @Autowired
    private PassportService passportService;

    @RequestMapping("/user/showLogin")
    public String showLogin() {
        return "login";
    }

    @RequestMapping("/user/showRegister")
    public String showRegister() {
        return "register";
    }

    @RequestMapping("/user/check/{param}/{type}")
    @ResponseBody
    public EgoResult checkUser(@PathVariable String param, @PathVariable int type) {
        TbUser tbUser = new TbUser();
        if (type == 1) {
            tbUser.setUsername(param);
        } else if (type == 3) {
            tbUser.setEmail(param);
        } else if (type == 2) {
            tbUser.setPhone(param);
        }
        return passportService.checkUser(tbUser);
    }

    @RequestMapping("/user/register")
    @ResponseBody
    public EgoResult registryUser(TbUser tbUser, String pwdRepeat) {
        return passportService.registerUser(tbUser);
    }

    @RequestMapping("/user/login")
    @ResponseBody
    public EgoResult login(TbUser tbUser, HttpSession session) {
        EgoResult er = passportService.login(tbUser);
        if (er.getStatus() == 200) {
            session.setAttribute("loginUser", er.getData()); // 客户端也不需要，且用户密码等敏感信息都没处理，不要返回。
            er.setData(null);
        }
        return er;
    }

    @RequestMapping("/user/token/{token}")
    @ResponseBody // 异步请求携带 cookie 时，需要设置 allowCredentials=true 表示允许接收 cookie 数据
    @CrossOrigin(allowCredentials = "true")
    public EgoResult token(HttpSession session) {
        Object obj = session.getAttribute("loginUser");
        if (obj != null) {
            TbUser tbUser = (TbUser) obj;
            tbUser.setPassword("");
            return EgoResult.ok(tbUser);
        }
        return EgoResult.error("获取用户信息失败");
    }

    @RequestMapping("/user/logout/{token}")
    @ResponseBody
    @CrossOrigin(allowCredentials = "true")
    public EgoResult logout(HttpSession session) {
        session.invalidate();
        return EgoResult.ok();
    }
}
