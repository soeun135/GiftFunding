package com.soeun.GiftFunding.domain.member.service;

import com.soeun.GiftFunding.dto.ReissueResponse;
import com.soeun.GiftFunding.domain.member.dto.Signin;
import com.soeun.GiftFunding.domain.member.dto.Signup;
import com.soeun.GiftFunding.domain.member.dto.UpdateInfo;
import com.soeun.GiftFunding.domain.member.dto.UpdateInfo.Response;
import com.soeun.GiftFunding.dto.MemberAdapter;
import com.soeun.GiftFunding.domain.member.dto.UserInfoResponse;
import org.springframework.security.core.userdetails.UserDetailsService;


public interface MemberService extends UserDetailsService{

    /**
     * 회원가입 메소드
     */
    Signup.Response signUp(Signup.Request request);

    /**
     * 로그인 메소드
     */
    Signin.Response signIn(Signin.Request request);

    /**
     * Access Token 재발급 메소드
     */
    ReissueResponse reissue(String request);

    /**
     * 사용자 정보 + 펀딩 상품 조회 메소드
     */
    UserInfoResponse userInfo(MemberAdapter memberAdapter);

    /**
     * 사용자 정보 변경 메소드
     */
    Response update(UpdateInfo.Request request, MemberAdapter memberAdapter);
}
