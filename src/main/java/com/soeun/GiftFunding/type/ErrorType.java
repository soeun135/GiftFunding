package com.soeun.GiftFunding.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorType {
    INTERNAL_SERVER_ERROR(500, "내부 서버 오류가 발생했습니다."),

    INVALID_TOKEN(400, "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED(400, "토큰 유효시간이 경과했습니다."),
    REFRESHTOKEN_EXPIRED(400, "리프레시 토큰의 유효기간이 경과했습니다. 재로그인 하세요."),
    OAUTH_ERROR(400, "OAUTH 인증에서 문제가 발생했습니다."),

    USER_DUPLICATED(400, "중복된 이메일입니다."),

    USER_NOT_FOUND(400, "회원 정보가 없습니다."),
    PASSWORD_UNMATCHED(400, "비밀번호가 일치하지 않습니다."),

    NOT_ALLOWED_YOURSELF(400, "자기자신에게 요청을 보낼 수 없습니다."),
    ALREADY_SEND_REQUEST(400, "이미 해당 사용자에게 친구 요청을 보냈습니다."),
    ALREADY_FRIEND_MEMBER(400, "이미 친구인 사용자 입니다."),
    ALREADY_RECEIVE_FRIEND_REQUEST(400, "상대방이 이미 친구요청을 보냈습니다."),
    REQUEST_NOT_FOUND(400, "입력한 아이디로 들어온 친구 요청이 없습니다."),
    FRIEND_INFO_NOT_FOUND(400, "친구가 아닌 사용자 입니다."),

    PRODUCT_NOT_FOUND(400, "존재하지 않는 상품입니다."),
    FUNDING_NOT_FOUND(400, "진행 중인 펀딩이 없습니다."),

    BALANCE_NOT_ENOUGH(400, "지갑 잔액이 충분하지 않습니다."),
    SUPPORT_EXCEED_WHOLE_PRICE(400, "후원 금액은 상품 금액을 초과할 수 없습니댜.");

    private final int code;
    private final String description;
}
