package com.soeun.GiftFunding.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    USER_DUPLICATED("중복된 이메일입니다.");

    private final String description;
}
