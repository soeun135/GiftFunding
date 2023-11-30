package com.soeun.GiftFunding.OAuth;

import com.soeun.GiftFunding.OAuth.kakao.KakaoLoginRequest;
import com.soeun.GiftFunding.OAuth.naver.NaverLoginRequest;
import com.soeun.GiftFunding.dto.Signin;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/oauth")
public class OAuthLoginController {
    private final OAuthLoginService oAuthLoginService;

    @PostMapping("/kakao")
    public ResponseEntity<Signin.Response> kakaoLogin(
        @RequestBody KakaoLoginRequest requset) {

        return ResponseEntity.ok(oAuthLoginService.login(requset));
    }

    @PostMapping("/naver")
    public ResponseEntity<Signin.Response> naverLogin(
        @RequestBody NaverLoginRequest request) {

        return ResponseEntity.ok(oAuthLoginService.login(request));
    }
    @GetMapping("/kakao/callback?code={code}")
    public String kakoCallback(@RequestParam String code) {
        return "\"카카오 인증 완료\" 코드 값 : " + code;
    }

}
