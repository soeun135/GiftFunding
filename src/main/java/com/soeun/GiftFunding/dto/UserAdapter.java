package com.soeun.GiftFunding.dto;

import com.soeun.GiftFunding.entity.Member;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserAdapter implements UserDetails {
    private final Member member; //인증 완료된 Member 객체
    private final String username; //인증완료된 Member ID
    private final String password; //인증완료된 Member 패스워드

    public UserAdapter(Member member, String username, String password) {
        this.member = member;
        this.username = username;
        this.password = password;
    }

    public Member getMember() {
        return this.member;
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
