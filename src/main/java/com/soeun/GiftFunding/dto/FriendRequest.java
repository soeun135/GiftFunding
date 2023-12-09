package com.soeun.GiftFunding.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class FriendRequest {
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private String email;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private String email;
        private static final String message = "님에게 친구요청을 보냈습니다.";
        public static String toResponse(String email) {
            return String.format(
                "[%s] %s", email, message);
        }
    }
}
