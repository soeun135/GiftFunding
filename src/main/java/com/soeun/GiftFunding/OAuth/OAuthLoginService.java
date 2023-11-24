package com.soeun.GiftFunding.OAuth;

import com.soeun.GiftFunding.entity.User;
import com.soeun.GiftFunding.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuthLoginService {
    private final UserRepository userRepository;
    private final AuthTokensGenerator authTokensGenerator;
    private final RequestOAuthInfoService requestOAuthInfoService;

    public AuthTokens login(OAuthLoginParams params) {
        OAuthInfoResponse oAuthInfoResponse =
            requestOAuthInfoService.request(params);
        String name = findOrCreateMember(
            oAuthInfoResponse, params).getName();

        return authTokensGenerator.generate(name);
    }

    private User findOrCreateMember(OAuthInfoResponse oAuthInfoResponse,OAuthLoginParams params) {
        return userRepository.findByName(oAuthInfoResponse.getNickname())
            .orElseGet(() -> newMember(oAuthInfoResponse, params));
    }

    private User newMember(OAuthInfoResponse oAuthInfoResponse,
        OAuthLoginParams params) {
        User user = User.builder()
            .name(oAuthInfoResponse.getNickname())
            .email(oAuthInfoResponse.getEmail())
            .phone(oAuthInfoResponse.getMobile())
            .oAuthProvider(oAuthInfoResponse.getOAuthProvider())
            .build();

        user = params.makeUserEntity(user, oAuthInfoResponse);

        return userRepository.save(user);
    }
}
