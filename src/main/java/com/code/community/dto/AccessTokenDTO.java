package com.code.community.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
public class AccessTokenDTO {
    private String code;
    private String client_id;
    private String client_secret;
    private String redirect_uri;
    private String state;
}
