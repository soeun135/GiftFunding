package com.soeun.GiftFunding.security.OAuth.kakao;

import com.soeun.GiftFunding.security.OAuth.dto.OAuthInfoResponse;
import com.soeun.GiftFunding.security.OAuth.dto.OAuthLoginRequest;
import com.soeun.GiftFunding.entity.Member;
import com.soeun.GiftFunding.type.OAuthProvider;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Getter
@NoArgsConstructor
@AllArgsConstructor
/**
 * 카카오 로그인에서 Request 객체로 사용.
 * 카카오 API 요청에 필요한 authorizationCode 를 갖고 있는 클래스
 */
public class KakaoLoginRequest implements OAuthLoginRequest {

    private String authorizationCode;
    private String email;
    private String phone;
    private String address;
    private String birthDay;

    public Member makeUserEntity(Member member, OAuthInfoResponse oAuthInfoResponse) {
        member.setEmail(this.email);
        member.setAddress(this.address);
        member.setPhone(this.phone);
        member.setBirthDay((LocalDate.parse(this.birthDay)));
        return member;
    }

    @Override
    public OAuthProvider oAuthProvider() {
        return OAuthProvider.KAKAO;
    }

    @Override
    public MultiValueMap<String, String> makeBody() {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", authorizationCode);
        return body;
    }
}
