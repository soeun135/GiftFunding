package com.soeun.GiftFunding.controller;

import com.soeun.GiftFunding.dto.*;
import com.soeun.GiftFunding.service.FriendService;
import com.soeun.GiftFunding.type.FriendState;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/friend")
public class FriendController {

    private final FriendService friendService;
    @GetMapping("/hi")
    public String test(//@AuthenticationPrincipal memberAdapter memberAdapter
    ) {
        return "hi";
    }
    @PostMapping("/request")
    //@RedissonLock
    public ResponseEntity<FriendRequest.Response> friendRequest(
        @RequestBody FriendRequest.Request request,
        @AuthenticationPrincipal MemberAdapter memberAdapter) {

        return ResponseEntity.ok(friendService.request(request, memberAdapter));
    }

    @GetMapping
    public ResponseEntity<Page<FriendList>> friendList(
        @AuthenticationPrincipal MemberAdapter memberAdapter,
        @RequestParam FriendState friendState,
        Pageable pageable) {

        return ResponseEntity.ok(friendService.friendList(
            memberAdapter, friendState, pageable));
    }


    @PatchMapping("/process")
    public ResponseEntity<FriendRequestProcess.Response> requestProcess(
        @AuthenticationPrincipal MemberAdapter memberAdapter,
        @RequestBody FriendRequestProcess.Request request) {

        return ResponseEntity.ok(friendService.requestProcess(memberAdapter, request));
    }

    @GetMapping("/funding-product/{id}")
    public ResponseEntity<FriendFundingProduct> friendProduct(
        @AuthenticationPrincipal MemberAdapter memberAdapter,
        @PathVariable Long id,
        Pageable pageable) {

        return ResponseEntity.ok(friendService.friendProduct(
            memberAdapter, id, pageable));
    }
}
