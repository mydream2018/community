package com.code.community.controller;

import com.code.community.mapper.UserMapper;
import com.code.community.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Controller
public class IndexController {

    @Autowired
    UserMapper userMapper;

    @GetMapping("/")
    public String hello(HttpServletRequest request){


        Cookie[] cookies = request.getCookies();
        //自己添加的cookie非空判断
        if(cookies == null){
            return "index";
        }
        for(Cookie cookie : cookies){
            if(cookie.getName().equals("token")){
                String token = cookie.getValue();
                User user = userMapper.findByToken(token);
                if(user != null){
//                    System.out.println("使用了cookie登录网站！");
                    request.getSession().setAttribute("user", user);
                }
                break;
            }
        }
        return "index";
    }
}
