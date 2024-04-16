package com.soeun.GiftFunding.security;

import com.soeun.GiftFunding.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetAuthentication {
    private final MemberService memberService;
    private final TokenProvider tokenProvider;

    public Authentication getAuthentication(String jwt) {
        UserDetails userDetails =
            this.memberService.loadUserByUsername(
                tokenProvider.getMail(jwt));

        return new UsernamePasswordAuthenticationToken(
            userDetails, "",
            userDetails.getAuthorities());
    }
}
