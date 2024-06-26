package com.soeun.GiftFunding.service;

import com.soeun.GiftFunding.dto.FriendFundingProduct;
import com.soeun.GiftFunding.dto.FriendList;
import com.soeun.GiftFunding.dto.FriendRequest;
import com.soeun.GiftFunding.dto.FriendRequestProcess;
import com.soeun.GiftFunding.dto.MemberAdapter;
import com.soeun.GiftFunding.type.FriendState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FriendService {
    /**
     * 친구 요청 메소드
     * 파라미터 : 요청 받는 대상 이메일, 로그인 한 사용자
     * 응답 : 대상 이메일 + "님에게 친구요청을 보냈습니다."
     */
    FriendRequest.Response request(
        FriendRequest.Request request, MemberAdapter memberAdapter);

    /**
     * 친구 목록 확인 메소드
     * 파라미터 : 로그인한 사용자 정보, FriendState, 페이지 정보
     * 응답 : FriendState가 WAIT, ACCEPT에 따라 다른 결과 조회
     */
    Page<FriendList> friendList(MemberAdapter memberAdapter, FriendState friendState, Pageable pageable);
    /**
     * 친구요청 수락/거절 메소드
     * 파라미터 : 로그인한 사용자, FriendRequestProcess.Request : 수락할 이메일, 수락/거절여부
     * 응답 : 이메일 + "의 친구요청을 수락/거절 했슴니다"
     */
    FriendRequestProcess.Response requestProcess(
            MemberAdapter memberAdapter, FriendRequestProcess.Request request);

    /**
     * 친구의 펀딩상품 조회 메소드
     * 파라미터 : 로그인한 사용자 정보, 친구 아이디, 페이지 정보
     * 응답 : 친구 정보 + 친구가 등록한 펀딩상품 중 진행중인 항목들     */
    FriendFundingProduct friendProduct(MemberAdapter memberAdapter, Long id, Pageable pageable);
}
