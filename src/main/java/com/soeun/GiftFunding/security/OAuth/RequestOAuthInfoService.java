package com.soeun.GiftFunding.security.OAuth;

import com.soeun.GiftFunding.security.OAuth.dto.OAuthInfoResponse;
import com.soeun.GiftFunding.security.OAuth.dto.OAuthLoginRequest;
import com.soeun.GiftFunding.type.OAuthProvider;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * 외부 API 요청의 중복되는 로직을 공통화한 클래스
 */
@Component
public class RequestOAuthInfoService {
    private final Map<OAuthProvider, OAuthApiClient> clients;

    public RequestOAuthInfoService(List<OAuthApiClient> clients) {
        this.clients = clients.stream().collect(
            Collectors.collectingAndThen(
                Collectors.toMap(OAuthApiClient::oAuthProvider, Function.identity()),
                Collections::unmodifiableMap
            )
        );
    }

    /**
     * access token을 통해 사용자 정보를 받아옴
     */
    public OAuthInfoResponse request (OAuthLoginRequest request) {
        OAuthApiClient client = clients.get(request.oAuthProvider());
        String accessToken = client.requestAccessToken(request);

        return client.requestOauthInfo(accessToken);
    }
}
