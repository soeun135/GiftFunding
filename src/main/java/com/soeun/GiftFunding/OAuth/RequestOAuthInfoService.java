package com.soeun.GiftFunding.OAuth;

import com.soeun.GiftFunding.type.OAuthProvider;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

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
    public OAuthInfoResponse request (OAuthLoginParams params) {
        OAuthApiClient client = clients.get(params.oAuthProvider());
        String accessToken = client.requestAccessToken(params);

        return client.requestOauthInfo(accessToken);
    }
}
