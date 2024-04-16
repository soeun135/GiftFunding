package com.soeun.GiftFunding.domain.fundingProduct.service;

import com.soeun.GiftFunding.dto.MemberAdapter;

public interface FundingService {

    /**
     * 펀딩상품 등록 메소드
     * 파라미터 : 상품 아이디, 로그인한 사용자 정보
     */
    void register(long productId, MemberAdapter memberAdapter);

    /**
     * 펀딩 상품 등록취소 메소드
     * 파라미터 : 펀딩 상품 아이디, 로그인한 사용자 정보
     */
    void cancel(long fundingId, MemberAdapter memberAdapter);
}
