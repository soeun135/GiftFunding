package com.soeun.GiftFunding.mock;

import com.soeun.GiftFunding.dto.MemberAdapter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithCustomMockUserSecurityContextFactory implements
        WithSecurityContextFactory<WithMockUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockUser annotation) {
        SecurityContext context =
                SecurityContextHolder.createEmptyContext();

        MemberAdapter memberAdapter =
                new MemberAdapter("sonfgi@naver.com", "abfgcd");

//                MemberAdapter.from(MemberFixture.soni.createMember());
//        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
//        grantedAuthorities.add((GrantedAuthority) () -> "ROLE_USER");

//        UsernamePasswordAuthenticationToken authentication =
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        memberAdapter, "password", null);


        context.setAuthentication(authentication);
        return context;
    }
}
