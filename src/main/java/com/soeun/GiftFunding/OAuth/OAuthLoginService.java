package com.soeun.GiftFunding.OAuth;

import com.soeun.GiftFunding.OAuth.dto.OAuthInfoResponse;
import com.soeun.GiftFunding.OAuth.dto.OAuthLoginRequest;
import com.soeun.GiftFunding.dto.Signin;
import com.soeun.GiftFunding.entity.Member;
import com.soeun.GiftFunding.entity.Wallet;
import com.soeun.GiftFunding.repository.MemberRepository;
import com.soeun.GiftFunding.repository.WalletRepository;
import com.soeun.GiftFunding.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuthLoginService {
    private final MemberRepository memberRepository;
    private final RequestOAuthInfoService requestOAuthInfoService;
    private final TokenProvider tokenProvider;
    private final WalletRepository walletRepository;

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

    private Member findOrCreateMember(OAuthInfoResponse oAuthInfoResponse, OAuthLoginRequest request) {
        return memberRepository.findByName(oAuthInfoResponse.getNickname())
            .orElseGet(() -> newMember(oAuthInfoResponse, request));
    }

    private Member newMember(OAuthInfoResponse oAuthInfoResponse,
        OAuthLoginRequest request) {
        Member member = Member.builder()
            .name(oAuthInfoResponse.getNickname())
            .email(oAuthInfoResponse.getEmail())
            .phone(oAuthInfoResponse.getMobile())
            .oAuthProvider(oAuthInfoResponse.getOAuthProvider())
            .build();

        member = request.makeUserEntity(member, oAuthInfoResponse);

        walletRepository.save(
            Wallet.builder()
                .balance(0L)
                .member(member)
                .build()
        );
        return memberRepository.save(member);
    }
}
