package com.soeun.GiftFunding.domain.support.controller;

import com.soeun.GiftFunding.domain.support.service.SupportService;
import com.soeun.GiftFunding.dto.SupportCancelResponse;
import com.soeun.GiftFunding.dto.SupportInfo;
import com.soeun.GiftFunding.dto.MemberAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/support")
@RequiredArgsConstructor
public class SupportController {

    private final SupportService supportService;

    @PostMapping("/{id}")
    public ResponseEntity<SupportInfo.Response> support(
        @AuthenticationPrincipal MemberAdapter memberAdapter,
        @PathVariable long id,
        @RequestBody SupportInfo.Request request) {

        return ResponseEntity.ok(
            supportService.support(memberAdapter, id, request));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<SupportCancelResponse> supportCancel(
        @AuthenticationPrincipal MemberAdapter memberAdapter,
        @PathVariable long id) {

        return ResponseEntity.ok(
            supportService.supportCancel(memberAdapter, id));
    }
}
