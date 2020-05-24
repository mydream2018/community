package com.code.community.controller;

import com.code.community.dto.AccessTokenDTO;
import com.code.community.dto.GithubUser;
import com.code.community.mapper.UserMapper;
import com.code.community.model.User;
import com.code.community.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
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
    private UserMapper userMapper;

    @GetMapping("/callback")
    public String callback(@RequestParam("code") String code,
                           @RequestParam("state") String state,
                           HttpServletResponse response){
//        System.out.println("调用了函数callback");
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setClient_secret(secretId);
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri(redirectUri);
        accessTokenDTO.setState(state);
        String token = githubProvider.getAccessToken(accessTokenDTO);
//        System.out.println(token);
        GithubUser githubUser = githubProvider.getUser(token);
//        System.out.println(githubUser.getName() + "--" + githubUser.getId() + "--" + githubUser.getBio());
        if(githubUser != null){
            User user = new User();
            String userToken = UUID.randomUUID().toString();
            user.setToken(userToken);
            user.setName(githubUser.getName());
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            user.setAccountId(String.valueOf(githubUser.getId()));
            userMapper.insert(user);
//            request.getSession().setAttribute("user", githubUser);
            response.addCookie(new Cookie("token", userToken));
            return "redirect:/";
        }else {
            return "redirect:/";
        }

    }

}
