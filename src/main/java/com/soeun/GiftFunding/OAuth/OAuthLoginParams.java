package com.soeun.GiftFunding.OAuth;

import com.soeun.GiftFunding.entity.User;
import com.soeun.GiftFunding.type.OAuthProvider;
import org.springframework.util.MultiValueMap;

/**
 * OAuth 요청을 위한 파라미터 값들을 갖고 있는 인터페이스
 * 컨트롤러의 요청 값으로 구현해서 사용
 */

public interface OAuthLoginParams {
    User makeUserEntity(User user, OAuthInfoResponse oAuthInfoResponse);
    OAuthProvider oAuthProvider();
    MultiValueMap<String, String> makeBody();
}
