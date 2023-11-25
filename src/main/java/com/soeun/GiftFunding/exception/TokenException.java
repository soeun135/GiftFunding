package com.soeun.GiftFunding.exception;

import com.soeun.GiftFunding.type.ErrorCode;
import lombok.Getter;

@Getter
public class TokenException extends RuntimeException{
    private final ErrorCode errorCode;
    private final String errorMessage;

    public TokenException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }
}
