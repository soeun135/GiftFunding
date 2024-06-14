package com.soeun.GiftFunding.controller;

import com.soeun.GiftFunding.dto.Signin;
import com.soeun.GiftFunding.dto.Signup;
import com.soeun.GiftFunding.dto.UpdateInfo;
import com.soeun.GiftFunding.dto.UserInfoResponse;
import com.soeun.GiftFunding.service.MemberService;
import com.soeun.GiftFunding.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
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
