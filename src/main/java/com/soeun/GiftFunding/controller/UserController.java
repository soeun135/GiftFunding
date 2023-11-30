package com.soeun.GiftFunding.controller;

import com.soeun.GiftFunding.dto.ReissueResponse;
import com.soeun.GiftFunding.dto.Signin;
import com.soeun.GiftFunding.dto.Signup;
import com.soeun.GiftFunding.dto.UpdateInfo;
import com.soeun.GiftFunding.dto.UserInfoResponse;
import com.soeun.GiftFunding.security.TokenProvider;
import com.soeun.GiftFunding.service.UserService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
public class UserController {
    private final UserService userService;
    private final TokenProvider tokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<Signup.Response> signUp(
        @RequestBody @Valid Signup.Request request) {

        return ResponseEntity.ok(
            userService.singUp(request));
    }

    @PostMapping("/signin")
    public ResponseEntity<Signin.Response> signIn(
        @RequestBody @Valid Signin.Request request) {
        return ResponseEntity.ok(userService.signIn(request));
    }

    @PostMapping("/reissue")
    public ResponseEntity<ReissueResponse> reissue(
        @RequestHeader("Authorization") String request) {
        return ResponseEntity.ok(userService.reissue(request));
    }

    @GetMapping("/info")
    public ResponseEntity<UserInfoResponse> userInfo(
        @RequestHeader("Authorization") String token) {

        return ResponseEntity.ok(userService.userInfo(token));
    }

    @PatchMapping("/update")
    public ResponseEntity<UpdateInfo.Response> update(
        @RequestBody UpdateInfo.Request request,
        @RequestHeader ("Authorization") String token) {

        return ResponseEntity.ok(userService.update(request, token));
    }

    @GetMapping("/access/token")
    public String test() {
        return "정상토큰 접근";
    }
}
