package com.soeun.GiftFunding.service;

import com.soeun.GiftFunding.dto.FriendRequest;
import com.soeun.GiftFunding.dto.FriendRequestList;
import com.soeun.GiftFunding.dto.UserAdapter;
import java.awt.print.Pageable;
import java.util.List;
import org.springframework.data.domain.Page;

public interface FriendService {
    /**
     * 친구 요청 메소드
     * 파라미터 : 요청 받는 대상 이메일, 로그인 한 사용자
     * 응답 : 대상 이메일 + "님에게 친구요청을 보냈습니다."
     */
    FriendRequest.Response request(
        FriendRequest.Request request, UserAdapter userAdapter);

    /**
     * 친구요청 목록 확인 메소드
     * 응답 : 로그인한 사용자의 아이디로 FriendState가 WAIT인 컬럼 조회
     */
    Page<FriendRequestList> requestList(UserAdapter userAdapter, Pageable pageable);
}
