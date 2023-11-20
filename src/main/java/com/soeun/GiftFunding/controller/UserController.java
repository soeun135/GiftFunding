package com.soeun.GiftFunding.controller;

import com.soeun.GiftFunding.dto.Signup;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    @PostMapping("/signup")
    public ResponseEntity<Signup.Response> signUp(
        @RequestBody @Valid Signup.Request request) {

        return null;
    }
}
