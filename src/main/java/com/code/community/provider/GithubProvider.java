package com.code.community.provider;

import com.alibaba.fastjson.JSON;
import com.code.community.dto.AccessTokenDTO;
import com.code.community.dto.GithubUser;
import okhttp3.*;
import org.springframework.stereotype.Component;


import java.io.IOException;

@Component
public class GithubProvider {

    public String getAccessToken(AccessTokenDTO accessTokenDTO){
        MediaType mediaType
                = MediaType.get("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();
//        System.out.println("到这里都是没问题");
        RequestBody body = RequestBody.create(mediaType,JSON.toJSONString(accessTokenDTO));
        Request request = new Request.Builder()
                .url("https://github.com/login/oauth/access_token")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
//这是下面的mybody返回数据的格式，需要做字符串切分，得到想要的token
            String mybody = response.body().string();
            String[] strs = mybody.split("&");
            String token = strs[0].split("=")[1];
//            System.out.println(token);
            return token;
        }
        catch (IOException e) {
                e.printStackTrace();
            }
        return null;
    }


    public GithubUser getUser(String accessToken){
        OkHttpClient client = new OkHttpClient();
        String url="https://api.github.com/user?access_token=" + accessToken;
        Request request = new Request.Builder()
                    .url(url)
                    .build();

        Response response = null;
        GithubUser user = null;
        try {
            response = client.newCall(request).execute();
            user = JSON.parseObject(response.body().string(),GithubUser.class);
            return user;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
