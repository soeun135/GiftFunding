package com.soeun.GiftFunding.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


public class TestResponse {

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    public static class Get{
        String message;
    }
}
