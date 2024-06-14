package com.soeun.GiftFunding.security.OAuth.naver;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.soeun.GiftFunding.security.OAuth.dto.OAuthInfoResponse;
import com.soeun.GiftFunding.type.OAuthProvider;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class NaverInfoResponse implements OAuthInfoResponse {
    @JsonProperty("response")
    private Response response;

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Response {
        private String email;
        private String nickname;
        private String birthday;
        private String birthyear;
        private String mobile;
    }
    @Override
    public String getNickname() {
        return response.nickname;
    }

    @Override
    public String getEmail() {
        return response.email;
    }

    @Override
    public String getBirthDay() {
        return response.birthday;
    }

    @Override
    public String getMobile() {
        return response.mobile;
    }

    @Override
    public String getBirthYear() {
        return response.birthyear;
    }

    @Override
    public OAuthProvider getOAuthProvider() {
        return OAuthProvider.NAVER;
    }
}
