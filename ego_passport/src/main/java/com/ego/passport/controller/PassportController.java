package com.ego.passport.controller;

import com.ego.commons.pojo.EgoResult;
import com.ego.passport.service.PassportService;
import com.ego.pojo.TbUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PassportController {
    @Autowired
    private PassportService passportService;

    @RequestMapping("/user/showLogin")
    public String showLogin(){
        return "login";
    }

    @RequestMapping("/user/showRegister")
    public String showRegister(){
        return "register";
    }

    @RequestMapping("/user/check/{param}/{type}")
    @ResponseBody
    public EgoResult checkUser(@PathVariable String param, @PathVariable int type){
        TbUser tbUser = new TbUser();
        if (type == 1){
            tbUser.setUsername(param);
        }else if (type == 3){
            tbUser.setEmail(param);
        }else if (type == 2){
            tbUser.setPhone(param);
        }
        return passportService.checkUser(tbUser);
    }

    @RequestMapping("/user/register")
    @ResponseBody
    public EgoResult registryUser(TbUser tbUser,String pwdRepeat){
            return passportService.registerUser(tbUser);
    }
}
