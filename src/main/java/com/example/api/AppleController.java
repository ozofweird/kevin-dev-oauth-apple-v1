package com.example.api;

import com.example.config.oauth.AppleService;
import com.example.config.oauth.dto.AppleAccessTokenRes;
import com.example.config.oauth.dto.AppleUserInfoRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class AppleController {

    private final AppleService appleService;

    @GetMapping("/")
    public String test() {
        return "test";
    }

    @GetMapping("/oauth/apple")
    public String getRedirectUri() {
        return appleService.getRedirectUri();
    }

    @GetMapping("/oauth/apple/callback")
    public String getCallback() {
        /**
         * Google과 Facebook은 파라미터로 결과를 전달받지만,
         * Apple은 '#(anchor, fragement)' 형태로 정보를 받아오기 때문에 REST API에서 처리할 수 없음
         *
         * 즉, 직접 결과값인 'state, code, id_token'을 가져와야함
         * - id_token 만료 기간 10분
         * - authorization_code 만료 기간 5분
         */

        return "callback";
    }

    @GetMapping("/oauth/apple/accessToken")
    public AppleAccessTokenRes getAccessToken(@RequestParam(name = "state") String state,
                                              @RequestParam(name = "code") String authorizationCode,
                                              @RequestParam(name = "id_token") String idToken) {
        log.info("--------------------------------------");
        log.info("State: " + state);
        log.info("AuthorizationCode: " + authorizationCode);
        log.info("IdToken: " + idToken);

        /**
         * AppleService.getRedirectUri()
         * - response_type=code id_token
         * - response_mode=fragement
         *
         * 로그인을 하고 나서 받은 결과로 'access_token, expires_in, id_token, refresh_token, token_type' 정보를 받아오도록 구성
         * access_token으로 인증해줄 서비스를 제공하지 않기 때문에 만료기간이 없는 refresh_token을 가져오도록 구성 필요
         * iOS SDK를 사용한다면, 'state, code, id_token, user(최초 가입시)'의 데이터만 반환해준다.
         */

        return appleService.getAccessToken(authorizationCode);
    }

    @GetMapping("/oauth/apple/userInfo")
    public AppleUserInfoRes getUserInfo(@RequestParam(name = "id_token") String idToken) {

        /**
         * 일반적으로 클라이언트(AOS, iOS)에서 공급자(Apple)로 부터 id_token값을 전달 받기 때문에 서버와 공급자간의 인증 과정은 불필요
         */
        return appleService.getUserId(idToken);
    }
}
