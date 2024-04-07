package com.soeun.GiftFunding.mock;

import com.soeun.GiftFunding.entity.Member;

import java.time.LocalDate;


public enum MemberFixture {
    soni(1L, "박소은", "010-1234-5678", "soni@naver.com", "abcd", "서울특별시 강남구", LocalDate.of(2000, 01, 28));

    private Long id;
    private String name;
    private String phone;
    private String email;
    private String password;
    private String address;
    private LocalDate birthDay;

    MemberFixture(final Long id, final String name,
                  final String phone, final String email,
                  final String password, final String address, final LocalDate birthDay) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.address = address;
        this.birthDay = birthDay;
    }

    public Member createMember() {
        return Member.builder()
                .id(this.id)
                .name(this.name)
                .phone(this.phone)
                .email(this.email)
                .password(this.password)
                .address(this.address)
                .birthDay(this.birthDay)
                .build();
    }
}
