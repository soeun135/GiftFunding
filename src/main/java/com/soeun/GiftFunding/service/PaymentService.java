package com.soeun.GiftFunding.service;

public interface PaymentService {

    void charge(long amount, long memberId);
}
