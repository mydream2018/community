package com.code.community.controller;

import com.code.community.dto.AccessTokenDTO;
import com.code.community.dto.GithubUser;
import com.code.community.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

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


    @GetMapping("/callback")
    public String callback(@RequestParam("code") String code,
                           @RequestParam("state") String state,
                           HttpServletRequest request){
//        System.out.println("调用了函数callback");
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setClient_secret(secretId);
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri(redirectUri);
        accessTokenDTO.setState(state);
        String token = githubProvider.getAccessToken(accessTokenDTO);
        GithubUser githubUser = githubProvider.getUser(token);
//        System.out.println(githubUser.getName() + "--" + githubUser.getId() + "--" + githubUser.getBio());
        if(githubUser != null){
            request.getSession().setAttribute("user", githubUser);
            return "redirect:/";
        }else {
            return "redirect:/";
        }

    }

}
