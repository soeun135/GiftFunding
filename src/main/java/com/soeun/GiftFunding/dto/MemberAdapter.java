package com.soeun.GiftFunding.dto;

import com.soeun.GiftFunding.entity.Member;
import lombok.Getter;

@Getter
public class MemberAdapter extends Member {
    private Member member;

    public MemberAdapter(Member member) {
        super(member.getId(), member.getName(), member.getPhone(),
            member.getEmail(), member.getPassword(), member.getAddress()
        , member.getBirthDay(), member.getOAuthProvider(), member.getCreatedAt(),
            member.getUpdatedAt());
        this.member = member;
    }
}
