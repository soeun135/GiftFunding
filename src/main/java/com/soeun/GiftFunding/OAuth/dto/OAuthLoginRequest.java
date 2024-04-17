package com.soeun.GiftFunding.OAuth.dto;

import com.soeun.GiftFunding.domain.member.entity.Member;
import com.soeun.GiftFunding.type.OAuthProvider;
import org.springframework.util.MultiValueMap;

/**
 * OAuth 요청을 위한 파라미터 값들을 갖고 있는 인터페이스
 * 컨트롤러의 요청 값으로 구현해서 사용
 */

public interface OAuthLoginRequest {
    Member makeUserEntity(Member member, OAuthInfoResponse oAuthInfoResponse);
    OAuthProvider oAuthProvider();
    MultiValueMap<String, String> makeBody();
}
