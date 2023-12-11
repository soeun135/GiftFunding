package com.soeun.GiftFunding.controller;

import com.soeun.GiftFunding.dto.ReissueResponse;
import com.soeun.GiftFunding.dto.Signin;
import com.soeun.GiftFunding.dto.Signup;
import com.soeun.GiftFunding.dto.UpdateInfo;
import com.soeun.GiftFunding.dto.UserAdapter;
import com.soeun.GiftFunding.dto.UserInfoResponse;
import com.soeun.GiftFunding.service.MemberService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Slf4j
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<Signup.Response> signUp(
        @RequestBody @Valid Signup.Request request) {

        return ResponseEntity.ok(
            memberService.signUp(request));
    }

    @PostMapping("/signin")
    public ResponseEntity<Signin.Response> signIn(
        @RequestBody @Valid Signin.Request request) {
        return ResponseEntity.ok(memberService.signIn(request));
    }

    @PostMapping("/reissue")
    public ResponseEntity<ReissueResponse> reissue(
        @RequestHeader("Authorization") String request) {
        return ResponseEntity.ok(memberService.reissue(request));
    }

    @GetMapping("/info")
    public ResponseEntity<UserInfoResponse> userInfo(
        @AuthenticationPrincipal UserAdapter userAdapter) {
        return ResponseEntity.ok(memberService.userInfo(userAdapter));
    }

    @PatchMapping("/update")
    public ResponseEntity<UpdateInfo.Response> update(
        @RequestBody UpdateInfo.Request request,
        @AuthenticationPrincipal UserAdapter userAdapter) {

        return ResponseEntity.ok(memberService.update(request, userAdapter));
    }
}
