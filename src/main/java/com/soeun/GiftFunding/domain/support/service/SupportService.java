package com.soeun.GiftFunding.domain.support.service;

import com.soeun.GiftFunding.dto.SupportCancelResponse;
import com.soeun.GiftFunding.dto.SupportInfo;
import com.soeun.GiftFunding.dto.MemberAdapter;

public interface SupportService {
    /**
     * 후원하기 메소드
     * 파라미터 : 로그인 한 사용자, 펀딩 상품 아이디, 후원할 금액
     * 응답 : 후원한 펀딩 상품의 상태
     */
    SupportInfo.Response support(
            MemberAdapter memberAdapter, long id, SupportInfo.Request request);

    /**
     * 후원취소 메소드
     * 파라미터 : 로그인 한 사용자, 후원 상세 내역 ID
     * 응답 :
     */
    SupportCancelResponse supportCancel(MemberAdapter memberAdapter, long id);
}
