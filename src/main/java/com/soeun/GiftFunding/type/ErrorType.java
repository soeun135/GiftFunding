package com.soeun.GiftFunding.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorType {
    INTERNAL_SERVER_ERROR(500, "내부 서버 오류가 발생했습니다."),

    INVALID_TOKEN(400, "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED(400, "토큰 유효시간이 경과했습니다."),

    OAUTH_ERROR(400, "OAUTH 인증에서 문제가 발생했습니다."),

    USER_DUPLICATED(400, "중복된 이메일입니다."),

    USER_NOT_FOUND(400, "회원 정보가 없습니다."),
    PASSWORD_UNMATCHED(400, "비밀번호가 일치하지 않습니다.");

    private final int code;
    private final String description;
}
