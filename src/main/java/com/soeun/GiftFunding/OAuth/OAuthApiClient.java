package com.soeun.GiftFunding.OAuth;

import com.soeun.GiftFunding.type.OAuthProvider;

public interface OAuthApiClient {
    OAuthProvider oAuthProvider();
    String requestAccessToken(OAuthLoginParams params);

    /**
     * 현재 로그인한 사용자 정보를 불러옴
     */
    OAuthInfoResponse requestOauthInfo(String accessToken);
}
