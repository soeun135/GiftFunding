package com.soeun.GiftFunding.OAuth;

import com.soeun.GiftFunding.OAuth.dto.OAuthInfoResponse;
import com.soeun.GiftFunding.OAuth.dto.OAuthLoginRequest;
import com.soeun.GiftFunding.dto.Signin;
import com.soeun.GiftFunding.entity.User;
import com.soeun.GiftFunding.repository.UserRepository;
import com.soeun.GiftFunding.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuthLoginService {
    private final UserRepository userRepository;
    private final RequestOAuthInfoService requestOAuthInfoService;
    private final TokenProvider tokenProvider;

    public Signin.Response login(OAuthLoginRequest request) {
        OAuthInfoResponse oAuthInfoResponse =
            requestOAuthInfoService.request(request);
        String mail = findOrCreateMember(
            oAuthInfoResponse, request).getEmail();

        String accessToken = tokenProvider.generateAccessToken(mail);
        String refreshToken = tokenProvider.generateRefreshToken(mail);

        return Signin.Response.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
    }

    private User findOrCreateMember(OAuthInfoResponse oAuthInfoResponse, OAuthLoginRequest request) {
        return userRepository.findByName(oAuthInfoResponse.getNickname())
            .orElseGet(() -> newMember(oAuthInfoResponse, request));
    }

    private User newMember(OAuthInfoResponse oAuthInfoResponse,
        OAuthLoginRequest request) {
        User user = User.builder()
            .name(oAuthInfoResponse.getNickname())
            .email(oAuthInfoResponse.getEmail())
            .phone(oAuthInfoResponse.getMobile())
            .oAuthProvider(oAuthInfoResponse.getOAuthProvider())
            .build();

        user = request.makeUserEntity(user, oAuthInfoResponse);

        return userRepository.save(user);
    }
}
