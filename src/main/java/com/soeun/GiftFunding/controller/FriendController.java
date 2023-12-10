package com.soeun.GiftFunding.controller;

import com.soeun.GiftFunding.dto.FriendRequest;
import com.soeun.GiftFunding.dto.FriendRequestList;
import com.soeun.GiftFunding.dto.UserAdapter;
import com.soeun.GiftFunding.service.FriendService;
import java.awt.print.Pageable;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @GetMapping("/request")
    public ResponseEntity<Page<FriendRequestList>> requestList(
        @AuthenticationPrincipal UserAdapter userAdapter,
        Pageable pageable) {

        return ResponseEntity.ok(friendService.requestList(userAdapter, pageable));
    }
}
