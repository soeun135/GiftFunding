package com.soeun.GiftFunding.exception;

import com.soeun.GiftFunding.type.ErrorType;
import lombok.Getter;

@Getter
public class PaymentException extends RuntimeException{
    private final ErrorType errorCode;
    private final String errorMessage;

    public PaymentException(ErrorType errorCode) {
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }
}
