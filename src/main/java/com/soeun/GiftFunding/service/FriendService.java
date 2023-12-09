package com.soeun.GiftFunding.service;

import com.soeun.GiftFunding.dto.FriendRequest;
import com.soeun.GiftFunding.dto.UserAdapter;

public interface FriendService {
    /**
     * 친구 요청 메소드
     * 파라미터 : 요청 받는 대상 이메일, 로그인 한 사용자
     * 응답 : 대상 이메일 + "님에게 친구요청을 보냈습니다."
     */
    String request(FriendRequest.Request request, UserAdapter userAdapter);
}
