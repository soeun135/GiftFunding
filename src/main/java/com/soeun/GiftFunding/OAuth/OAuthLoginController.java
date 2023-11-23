package com.soeun.GiftFunding.OAuth;

import com.soeun.GiftFunding.OAuth.kakao.KakaoLoginParams;
import com.soeun.GiftFunding.OAuth.naver.NaverLoginParams;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
//@RequestMapping("")
public class OAuthLoginController {
    private final OAuthLoginService oAuthLoginService;

    @PostMapping("/oauth/kakao")
    public ResponseEntity<AuthTokens> loginKakao(
        @RequestBody KakaoLoginParams params) {

        return ResponseEntity.ok(oAuthLoginService.login(params));
    }

    @PostMapping("/oauth/naver")
    public ResponseEntity<AuthTokens> loginNaver(
        @RequestBody NaverLoginParams params) {

        return ResponseEntity.ok(oAuthLoginService.login(params));
    }
    @GetMapping("/kakao/callback?code={code}")
    public String kakoCallback(@RequestParam String code) {
        return "\"카카오 인증 완료\" 코드 값 : " + code;
    }

    @GetMapping("/access/token")
    public String test() {
        return "정상토큰 접근";
    }
}
