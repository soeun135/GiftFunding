package com.soeun.GiftFunding.service;

<<<<<<< HEAD
import static com.soeun.GiftFunding.type.ErrorType.PRODUCT_NOT_FOUND;
=======
import static com.soeun.GiftFunding.type.ErrorType.FUNDING_NOT_FOUND;
import static com.soeun.GiftFunding.type.ErrorType.PRODUCT_NOT_FOUND;
import static com.soeun.GiftFunding.type.FundingState.CANCEL;
>>>>>>> feature/funding
import static com.soeun.GiftFunding.type.FundingState.ONGOING;

import com.soeun.GiftFunding.dto.UserAdapter;
import com.soeun.GiftFunding.entity.FundingProduct;
import com.soeun.GiftFunding.entity.Member;
import com.soeun.GiftFunding.entity.Product;
<<<<<<< HEAD
=======
import com.soeun.GiftFunding.entity.Wallet;
>>>>>>> feature/funding
import com.soeun.GiftFunding.exception.FriendException;
import com.soeun.GiftFunding.exception.FundingException;
import com.soeun.GiftFunding.repository.FundingProductRepository;
import com.soeun.GiftFunding.repository.MemberRepository;
import com.soeun.GiftFunding.repository.ProductRepository;
<<<<<<< HEAD
=======
import com.soeun.GiftFunding.repository.WalletRepository;
>>>>>>> feature/funding
import com.soeun.GiftFunding.type.ErrorType;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
<<<<<<< HEAD
=======
import org.springframework.transaction.annotation.Transactional;
>>>>>>> feature/funding

@Service
@RequiredArgsConstructor
public class FundingServiceImpl implements FundingService{
    private final ProductRepository productRepository;
    private final FundingProductRepository fundingProductRepository;
    private final MemberRepository memberRepository;
<<<<<<< HEAD

    @Override
    public void register(long id, UserAdapter userAdapter) {
        Product product = productRepository.findById(id)
=======
    private final WalletRepository walletRepository;
    @Override
    public void register(long productId, UserAdapter userAdapter) {
        Product product = productRepository.findById(productId)
>>>>>>> feature/funding
            .orElseThrow(() -> new FundingException(PRODUCT_NOT_FOUND));

        Member member = memberRepository.findByEmail(userAdapter.getUsername())
            .orElseThrow(() -> new FriendException(ErrorType.USER_NOT_FOUND));

        fundingProductRepository.save(
            FundingProduct.builder()
                .product(product)
                .member(member)
                .total(0L)
                .expiredAt(LocalDateTime.now().plusYears(1))
                .fundingState(ONGOING)
                .build()
        );
    }
<<<<<<< HEAD
=======

    @Override
    @Transactional
    public void cancel(long fundingId, UserAdapter userAdapter) {
        Member member = memberRepository.findByEmail(userAdapter.getUsername())
            .orElseThrow(() -> new FriendException(ErrorType.USER_NOT_FOUND));

        FundingProduct fundingProduct = fundingProductRepository.findById(fundingId)
            .orElseThrow(() -> new FundingException(FUNDING_NOT_FOUND));

        fundingProduct.setFundingState(CANCEL);

        long total = fundingProduct.getTotal();

        Wallet walletInfo = walletRepository.findByMember(member);
        if (total > 0) {
            walletInfo.setBalance(
                    walletInfo.getBalance() + total
                );
        }
    }
>>>>>>> feature/funding
}
