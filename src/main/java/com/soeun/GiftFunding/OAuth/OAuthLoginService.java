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
            oAuthInfoResponse, params);

        return authTokensGenerator.generate(name);
    }

    private String findOrCreateMember(OAuthInfoResponse oAuthInfoResponse,OAuthLoginParams params) {
        System.out.println("응답객체 " + oAuthInfoResponse);
        return userRepository.findByName(oAuthInfoResponse.getNickname())
            .map(User::getName)
            .orElseGet(() -> newMember(oAuthInfoResponse, params));
    }

    private String newMember(OAuthInfoResponse oAuthInfoResponse,
        OAuthLoginParams params) {
        User user = User.builder()
            .name(oAuthInfoResponse.getNickname())
            .email(oAuthInfoResponse.getEmail())
            .phone(oAuthInfoResponse.getMobile())
            .oAuthProvider(oAuthInfoResponse.getOAuthProvider())
            .build();

        user = params.makeUserEntity(user, oAuthInfoResponse);

        return userRepository.save(user).getName();
    }
}
