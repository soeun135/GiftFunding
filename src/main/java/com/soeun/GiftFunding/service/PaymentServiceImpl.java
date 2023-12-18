package com.soeun.GiftFunding.service;

import static com.soeun.GiftFunding.type.ErrorType.WALLET_NOT_FOUND;
import static com.soeun.GiftFunding.type.TransactionType.CHARGE;

import com.soeun.GiftFunding.entity.Transaction;
import com.soeun.GiftFunding.entity.Wallet;
import com.soeun.GiftFunding.exception.PaymentException;
import com.soeun.GiftFunding.repository.TransactionRepository;
import com.soeun.GiftFunding.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService{
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public void charge(long amount, long memberId) {
        Wallet wallet = walletRepository.findById(memberId)
            .orElseThrow(() -> new PaymentException(WALLET_NOT_FOUND));
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
