package com.soeun.GiftFunding.exception;

import com.soeun.GiftFunding.type.ErrorCode;

public class UserException extends RuntimeException{
    private final ErrorCode errorCode;
    private final String errorMessage;

    public UserException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }
}
