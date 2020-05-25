package com.code.community.controller;

import com.code.community.dto.PaginationDTO;
import com.code.community.dto.QuestionDTO;
import com.code.community.mapper.QuestionMapper;
import com.code.community.mapper.UserMapper;
import com.code.community.model.Question;
import com.code.community.model.User;
import com.code.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class IndexController {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/")
    public String hello(HttpServletRequest request,
                        Model model,
                        @RequestParam(value = "page", defaultValue = "1") Integer page,
                        @RequestParam(value = "size", defaultValue = "2") Integer size
                        ){
        PaginationDTO pagination = questionService.list(page, size);
//        Integer totalCount = questionMapper.count();
//        pagination.setPagination(totalCount, page, size);

        model.addAttribute("pagination", pagination);


        Cookie[] cookies = request.getCookies();
        //自己添加的cookie非空判断
        if(cookies == null || cookies.length == 0){
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
