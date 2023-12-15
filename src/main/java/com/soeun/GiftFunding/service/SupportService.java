package com.soeun.GiftFunding.service;

import com.soeun.GiftFunding.dto.SupportInfo;
import com.soeun.GiftFunding.dto.UserAdapter;

public interface SupportService {
    /**
     * 후원하기 메소드
     * 파라미터 : 로그인 한 사용자, 펀딩 상품 아이디, 후원할 금액
     * 응답 :
     */
    SupportInfo.Response support(
        UserAdapter userAdapter, long id, SupportInfo.Request request);
}
