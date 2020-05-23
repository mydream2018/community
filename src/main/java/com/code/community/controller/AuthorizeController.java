package com.code.community.controller;

import com.code.community.dto.AccessTokenDTO;
import com.code.community.dto.GithubUser;
import com.code.community.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthorizeController {
    @Autowired
    private GithubProvider githubProvider;

    @GetMapping("/callback")
    public String callback(@RequestParam("code") String code,
                           @RequestParam("state") String state){
//        System.out.println("调用了函数callback");
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setClient_id("5a7d4b587a6053a37b9c");
        accessTokenDTO.setClient_secret("6bd9542b0fe2ff65515d2ce45bd251ef95d003f9");
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri("http://localhost:8090/callback");
        accessTokenDTO.setState(state);
        String token = githubProvider.getAccessToken(accessTokenDTO);
        GithubUser githubUser = githubProvider.getUser(token);
//        System.out.println(githubUser.getName() + "--" + githubUser.getId() + "--" + githubUser.getBio());
        return "index";
    }

}
