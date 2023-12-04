package com.soeun.GiftFunding.OAuth.naver;

import static com.soeun.GiftFunding.type.ErrorType.OAUTH_ERROR;

import com.soeun.GiftFunding.OAuth.OAuthApiClient;
import com.soeun.GiftFunding.OAuth.dto.OAuthInfoResponse;
import com.soeun.GiftFunding.OAuth.dto.OAuthLoginRequest;
import com.soeun.GiftFunding.exception.MemberException;
import com.soeun.GiftFunding.type.OAuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class NaverApiClient implements OAuthApiClient {

    private static final String GRANT_TYPE = "authorization_code";

    private final RestTemplate restTemplate;

    @Value("${oauth.naver.secret}")
    private String secretKey;

    @Value("${oauth.naver.client-id}")
    private String clientId;

    @Value("${oauth.naver.url.auth}")
    private String authUrl;

    @Value("${oauth.naver.url.api}")
    private String apiUrl;

    private static final String ACCESS_TOKEN_URL = "/oauth2.0/token";

    private static final String OAUTH_INFO_URL = "/v1/nid/me";

    @Override
    public OAuthProvider oAuthProvider() {
        return OAuthProvider.NAVER;
    }

    @Override
    public String requestAccessToken(OAuthLoginRequest params) {
        String url = authUrl + ACCESS_TOKEN_URL;

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = params.makeBody();
        body.add("grant_type", GRANT_TYPE);
        body.add("client_id", clientId);
        body.add("client_secret", secretKey);

        HttpEntity<?> request = new HttpEntity<>(body, httpHeaders);

        NaverTokens response = restTemplate.postForObject(url, request, NaverTokens.class);

        if (response == null) {
            throw new MemberException(OAUTH_ERROR);
        }
        return response.getAccessToken();
    }

    @Override
    public OAuthInfoResponse requestOauthInfo(String accessToken) {
        String url = apiUrl + OAUTH_INFO_URL;

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpHeaders.set("Authorization", "Bearer " + accessToken);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

        HttpEntity<?> request = new HttpEntity<>(body, httpHeaders);

        return restTemplate.postForObject(url, request, NaverInfoResponse.class);
    }
}
