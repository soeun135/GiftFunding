package com.soeun.GiftFunding.service;

import com.soeun.GiftFunding.dto.ReissueResponse;
import com.soeun.GiftFunding.dto.Signin;
import com.soeun.GiftFunding.dto.Signup;
import com.soeun.GiftFunding.dto.UpdateInfo;
import com.soeun.GiftFunding.dto.UpdateInfo.Response;
import com.soeun.GiftFunding.dto.UserAdapter;
import com.soeun.GiftFunding.dto.UserInfoResponse;
import com.soeun.GiftFunding.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;


public interface UserService extends UserDetailsService{

    /**
     * 회원가입 메소드
     */
    Signup.Response singUp(Signup.Request request);

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
    UserInfoResponse userInfo(UserAdapter userAdapter);

    /**
     * 사용자 정보 변경 메소드
     */
    Response update(UpdateInfo.Request request, UserAdapter userAdapter);
}
