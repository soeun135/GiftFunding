package com.soeun.GiftFunding.dto;

import com.soeun.GiftFunding.entity.Member;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
@Builder
public class MemberAdapter implements UserDetails {
    private final String username; //인증완료된 Member ID
    private final String password; //인증완료된 Member 패스워드

    public MemberAdapter(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static MemberAdapter from(final Member member) {
        return MemberAdapter.builder()
                .username(member.getEmail())
                .password(member.getPassword())
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
