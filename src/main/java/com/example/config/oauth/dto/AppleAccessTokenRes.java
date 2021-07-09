package com.example.config.oauth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppleAccessTokenRes {

    private String access_token;
    private String token_type;
    private String expires_in;
    private String refresh_token;
    private String id_token;
}
