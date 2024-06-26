package com.soeun.GiftFunding.controller;

import com.soeun.GiftFunding.dto.MemberAdapter;
import com.soeun.GiftFunding.service.FundingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/funding")
public class FundingController {
    private final FundingService fundingService;

    @PostMapping("/register/{id}")
    public ResponseEntity<Void> register(
        @PathVariable long id,
        @AuthenticationPrincipal MemberAdapter memberAdapter) {

        fundingService.register(id, memberAdapter);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/cancel/{id}")
    public ResponseEntity<Void> cancel(
        @PathVariable long id,
        @AuthenticationPrincipal MemberAdapter memberAdapter) {

        fundingService.cancel(id, memberAdapter);
        return ResponseEntity.ok().build();
    }
}
