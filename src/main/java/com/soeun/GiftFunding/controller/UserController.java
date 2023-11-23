package com.soeun.GiftFunding.controller;

import com.soeun.GiftFunding.dto.Signin;
import com.soeun.GiftFunding.dto.Signin.Response;
import com.soeun.GiftFunding.dto.Signup;
import com.soeun.GiftFunding.security.TokenProvider;
import com.soeun.GiftFunding.service.UserService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    public ResponseEntity<String> signIn(
        @RequestBody @Valid Signin.Request request) {
        Response response = userService.signIn(request);

        return ResponseEntity.ok(
            this.tokenProvider.generateToken(response.getUserName(), response.getRole())
        );
    }

}
