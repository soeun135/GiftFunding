package com.soeun.GiftFunding.domain.member.controller;

import com.soeun.GiftFunding.domain.member.dto.Signin;
import com.soeun.GiftFunding.domain.member.dto.Signup;
import com.soeun.GiftFunding.domain.member.dto.UpdateInfo;
import com.soeun.GiftFunding.domain.member.dto.UserInfoResponse;
import com.soeun.GiftFunding.domain.member.service.MemberService;
import com.soeun.GiftFunding.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
        @AuthenticationPrincipal MemberAdapter memberAdapter) {
        return ResponseEntity.ok(memberService.userInfo(memberAdapter));
    }

    @PatchMapping("/update")
    public ResponseEntity<UpdateInfo.Response> update(
        @RequestBody UpdateInfo.Request request,
        @AuthenticationPrincipal MemberAdapter memberAdapter) {

        return ResponseEntity.ok(memberService.update(request, memberAdapter));
    }
}
