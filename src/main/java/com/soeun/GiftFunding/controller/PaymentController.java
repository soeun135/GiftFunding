package com.soeun.GiftFunding.controller;

import com.soeun.GiftFunding.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    @RequestMapping(value = "/pay/success", method = RequestMethod.POST)
    public void paySuccess(@RequestParam("payAmount") long amount,
        @RequestParam("memberId") long memberId) {

        paymentService.charge(amount, memberId);
    }
}
