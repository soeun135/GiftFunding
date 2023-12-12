package com.soeun.GiftFunding.controller;

import com.soeun.GiftFunding.dto.FriendFundingProduct;
import com.soeun.GiftFunding.dto.FriendList;
import com.soeun.GiftFunding.dto.FriendRequest;
import com.soeun.GiftFunding.dto.FriendRequestProcess;
import com.soeun.GiftFunding.dto.UserAdapter;
import com.soeun.GiftFunding.service.FriendService;
import com.soeun.GiftFunding.type.FriendState;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/friend")
public class FriendController {

    private final FriendService friendService;

    @PostMapping("/request")
    public ResponseEntity<FriendRequest.Response> friendRequest(
        @RequestBody FriendRequest.Request request,
        @AuthenticationPrincipal UserAdapter userAdapter) {

        return ResponseEntity.ok(friendService.request(request, userAdapter));
    }

    @GetMapping
    public ResponseEntity<Page<FriendList>> friendList(
        @AuthenticationPrincipal UserAdapter userAdapter,
        @RequestParam FriendState friendState,
        Pageable pageable) {

        return ResponseEntity.ok(friendService.friendList(
            userAdapter, friendState, pageable));
    }


    @PatchMapping("/process")
    public ResponseEntity<FriendRequestProcess.Response> requestProcess(
        @AuthenticationPrincipal UserAdapter userAdapter,
        @RequestBody FriendRequestProcess.Request request) {

        return ResponseEntity.ok(friendService.requestProcess(userAdapter, request));
    }

    @GetMapping("/funding-product/{id}")
    public ResponseEntity<FriendFundingProduct> friendProduct(
        @AuthenticationPrincipal UserAdapter userAdapter,
        @PathVariable Long id,
        Pageable pageable) {

        return ResponseEntity.ok(friendService.friendProduct(
            userAdapter, id, pageable));
    }
}
