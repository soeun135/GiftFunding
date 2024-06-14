package com.soeun.GiftFunding.service;

import static com.soeun.GiftFunding.type.ErrorType.WALLET_NOT_FOUND;
import static com.soeun.GiftFunding.type.TransactionType.CHARGE;

import com.soeun.GiftFunding.entity.Payment;
import com.soeun.GiftFunding.entity.Transaction;
import com.soeun.GiftFunding.entity.Wallet;
import com.soeun.GiftFunding.exception.PaymentException;
import com.soeun.GiftFunding.repository.PaymentRepository;
import com.soeun.GiftFunding.repository.TransactionRepository;
import com.soeun.GiftFunding.repository.WalletRepository;
import com.soeun.GiftFunding.type.PaymentMethod;
import com.soeun.GiftFunding.type.PaymentResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService{
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public void charge(long amount, long memberId) {
        Wallet wallet = walletRepository.findById(memberId)
            .orElseThrow(() -> new PaymentException(WALLET_NOT_FOUND));

        paymentRepository.save(
            Payment.builder()
                .wallet(wallet)
                .paymentAmount(amount)
                .paymentMethod(PaymentMethod.CARD)
                .paymentResult(PaymentResult.SUCCESS)
                .build()
        );
        transactionRepository.save(
            Transaction.builder()
                .wallet(wallet)
                .transactionType(CHARGE)
                .transactionAmount(amount)
                .build()
        );

        wallet.setBalance(wallet.getBalance() + amount);
    }
}
