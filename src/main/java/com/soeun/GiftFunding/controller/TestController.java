package com.soeun.GiftFunding.controller;

import com.soeun.GiftFunding.dto.TestResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class TestController {


    @GetMapping
    public ResponseEntity<TestResponse.Get> get() {
        return ResponseEntity.ok(
                new TestResponse.Get("냠"));
    }
}