package com.soeun.GiftFunding.service;

import com.soeun.GiftFunding.dto.UserAdapter;

public interface FundingService {

    /**
     * 펀딩상품 등록하는 메소드
     * 파라미터 : 상품 아이디, 로그인한 사용자 정보
     */
    void register(long id, UserAdapter userAdapter);
}
