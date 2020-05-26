package com.code.community.controller;

import com.code.community.dto.AccessTokenDTO;
import com.code.community.dto.GithubUser;
import com.code.community.mapper.UserMapper;
import com.code.community.model.User;
import com.code.community.provider.GithubProvider;
import com.code.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
public class AuthorizeController {

    @Autowired
    private GithubProvider githubProvider;

    @Value("${github.client.id}")
    private String clientId;

    @Value("${github.client.secretId}")
    private String secretId;

    @Value("${github.client.redirectUri}")
    private String redirectUri;

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/callback")
    public String callback(@RequestParam("code") String code,
                           @RequestParam("state") String state,
                           HttpServletResponse response,
                           Model model){
//        System.out.println("调用了函数callback");
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();

        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setClient_secret(secretId);
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri(redirectUri);
        accessTokenDTO.setState(state);
        String token = githubProvider.getAccessToken(accessTokenDTO);//        System.out.println(token);
        GithubUser githubUser = null;
        try {
            githubUser = githubProvider.getUser(token);
        } catch (Exception e) {
            //e.printStackTrace();
            model.addAttribute("errorMsg","github出了点问题，请重新登录");
            System.out.println("github抽风了，请尝试重新登录！");
            return "redirect:/";
        }
//        System.out.println(githubUser.getName() + "--" + githubUser.getId() + "--" + githubUser.getBio());
        //输出github 的id值
        System.out.println(githubUser.getId()+"------------------------------");
        if(githubUser != null && githubUser.getId() != null){
            User user = new User();
            String userToken = UUID.randomUUID().toString();
            user.setToken(userToken);
            user.setName(githubUser.getName());
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setBio(githubUser.getBio());
            user.setAvatarUrl(githubUser.getAvatarUrl());
//            userMapper.insert(user);
            userService.createOrUpdate(user);
//            request.getSession().setAttribute("user", githubUser);
            Cookie cookie = new Cookie("token", userToken);
            cookie.setMaxAge(60*60*24*10);
            response.addCookie(cookie);
            return "redirect:/";
        }else {
            return "redirect:/";
        }

    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request,
                         HttpServletResponse response){

        request.getSession().removeAttribute("user");
        Cookie cookie = new Cookie("token", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/";
    }
}
