package com.soeun.GiftFunding.security.OAuth.naver;

import com.soeun.GiftFunding.security.OAuth.dto.OAuthInfoResponse;
import com.soeun.GiftFunding.security.OAuth.dto.OAuthLoginRequest;
import com.soeun.GiftFunding.entity.Member;
import com.soeun.GiftFunding.type.OAuthProvider;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Getter
@NoArgsConstructor
/**
 * 카카오 로그인에서 Request 객체로 사용.
 * 카카오 API 요청에 필요한 authorizationCode 를 갖고 있는 클래스
 */
public class NaverLoginRequest implements OAuthLoginRequest {

    private String authorizationCode;
    private String state;
    private String address;

    public Member makeUserEntity(Member member, OAuthInfoResponse oAuthInfoResponse) {
        member.setAddress(this.address);
        member.setBirthDay(LocalDate.parse(oAuthInfoResponse.getBirthYear()+"-"+oAuthInfoResponse.getBirthDay()));
        return member;
    }

    @Override
    public OAuthProvider oAuthProvider() {
        return OAuthProvider.NAVER;
    }

    @Override
    public MultiValueMap<String, String> makeBody() {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", authorizationCode);
        body.add("state", state);
        return body;
    }
}
