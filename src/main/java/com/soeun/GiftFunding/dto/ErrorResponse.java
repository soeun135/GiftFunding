package com.soeun.GiftFunding.dto;

import com.soeun.GiftFunding.type.ErrorType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {
    private ErrorType errorCode;
    private String errorMessage;
    private int statusCode;

    public static ErrorResponse of(ErrorType errorType, String msg, int statusCode) {
        return ErrorResponse.builder()
            .errorCode(errorType)
            .errorMessage(msg)
            .statusCode(statusCode)
            .build();
    }
}
