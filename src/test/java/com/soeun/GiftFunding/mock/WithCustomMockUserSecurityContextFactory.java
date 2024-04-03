package com.soeun.GiftFunding.mock;

import com.soeun.GiftFunding.dto.UserAdapter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.ArrayList;
import java.util.List;

public class WithCustomMockUserSecurityContextFactory implements
        WithSecurityContextFactory<WithMockUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockUser annotation) {
        SecurityContext context =
                SecurityContextHolder.createEmptyContext();

        UserAdapter userAdapter =
//                new UserAdapter("soni@naver.com", "abcd");
//
        UserAdapter.from(MemberFixture.soni.createMember());
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add((GrantedAuthority) () -> "ROLE_USER");

        UsernamePasswordAuthenticationToken authentication =
//        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        userAdapter, "password", grantedAuthorities);


        context.setAuthentication(authentication);
//        SecurityContextHolder.setContext(context);
        return context;
    }
}
