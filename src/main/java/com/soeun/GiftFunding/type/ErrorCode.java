package com.soeun.GiftFunding.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    USER_DUPLICATED("중복된 이메일입니다."),

    USER_NOT_FOUND("회원 정보가 없습니다."),
    PASSWORD_UNMATCHED("비밀번호가 일치하지 않습니다.");

    private final String description;
}
