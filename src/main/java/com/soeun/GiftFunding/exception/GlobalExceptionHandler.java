package com.soeun.GiftFunding.exception;

import static com.soeun.GiftFunding.type.ErrorType.INTERNAL_SERVER_ERROR;

import com.soeun.GiftFunding.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MemberException.class)
    public ErrorResponse handleMemberException(MemberException e) {
        log.error("{} is occurred", e.getErrorCode());

        return new ErrorResponse(
            e.getErrorCode(),
            e.getErrorMessage(),
            e.getErrorCode().getCode());
    }

    @ExceptionHandler(TokenException.class)
    public ErrorResponse handleInvalidTokenException(TokenException e) {
        log.error("{} is occurred", e.getErrorCode());

        return new ErrorResponse(
            e.getErrorCode(),
            e.getErrorMessage(),
            e.getErrorCode().getCode());
    }

    @ExceptionHandler(FriendException.class)
    public ErrorResponse handleFriendException(FriendException e) {
        log.error("{} is occurred", e.getErrorCode());

        return new ErrorResponse(
            e.getErrorCode(),
            e.getErrorMessage(),
            e.getErrorCode().getCode());
    }

    @ExceptionHandler(FundingException.class)
    public ErrorResponse handleFundingException(FundingException e) {
        log.error("{} is occurred", e.getErrorCode());

        return new ErrorResponse(
            e.getErrorCode(),
            e.getErrorMessage(),
            e.getErrorCode().getCode());
    }

    @ExceptionHandler(SupportException.class)
    public ErrorResponse handleSupportException(SupportException e) {
        log.error("{} is occurred", e.getErrorCode());

        return new ErrorResponse(
            e.getErrorCode(),
            e.getErrorMessage(),
            e.getErrorCode().getCode());
    }

    @ExceptionHandler(PaymentException.class)
    public ErrorResponse handlePaymentException(PaymentException e) {
        log.error("{} is occurred", e.getErrorCode());

        return new ErrorResponse(
            e.getErrorCode(),
            e.getErrorMessage(),
            e.getErrorCode().getCode());
    }

    @ExceptionHandler(Exception.class)
    public ErrorResponse handleException(Exception e) {
        log.error("Exception is occurred", e);

        return new ErrorResponse(
            INTERNAL_SERVER_ERROR,
            INTERNAL_SERVER_ERROR.getDescription(),
            INTERNAL_SERVER_ERROR.getCode());
    }
}
