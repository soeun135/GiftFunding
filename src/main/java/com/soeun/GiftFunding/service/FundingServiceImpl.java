package com.soeun.GiftFunding.service;

import static com.soeun.GiftFunding.type.ErrorType.FUNDING_NOT_FOUND;
import static com.soeun.GiftFunding.type.ErrorType.PRODUCT_NOT_FOUND;
import static com.soeun.GiftFunding.type.FundingState.CANCEL;
import static com.soeun.GiftFunding.type.FundingState.ONGOING;

import com.soeun.GiftFunding.dto.UserAdapter;
import com.soeun.GiftFunding.entity.FundingProduct;
import com.soeun.GiftFunding.entity.Member;
import com.soeun.GiftFunding.entity.Product;
import com.soeun.GiftFunding.entity.Wallet;
import com.soeun.GiftFunding.exception.FriendException;
import com.soeun.GiftFunding.exception.FundingException;
import com.soeun.GiftFunding.repository.FundingProductRepository;
import com.soeun.GiftFunding.repository.MemberRepository;
import com.soeun.GiftFunding.repository.ProductRepository;
import com.soeun.GiftFunding.repository.WalletRepository;
import com.soeun.GiftFunding.type.ErrorType;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
public class FundingServiceImpl implements FundingService{
    private final ProductRepository productRepository;
    private final FundingProductRepository fundingProductRepository;
    private final MemberRepository memberRepository;

    private final WalletRepository walletRepository;
    @Override
    public void register(long productId, UserAdapter userAdapter) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new FundingException(PRODUCT_NOT_FOUND));

        Member member = memberRepository.findByEmail(userAdapter.getUsername())
            .orElseThrow(() -> new FriendException(ErrorType.USER_NOT_FOUND));

        fundingProductRepository.save(
            FundingProduct.builder()
                .product(product)
                .member(member)
                .total(0L)
                .expiredAt(LocalDate.now().plusYears(1))
                .fundingState(ONGOING)
                .build()
        );
    }

    @Override
    @Transactional
    public void cancel(long fundingId, UserAdapter userAdapter) {
        Member member = memberRepository.findByEmail(userAdapter.getUsername())
            .orElseThrow(() -> new FriendException(ErrorType.USER_NOT_FOUND));

        FundingProduct fundingProduct =
            fundingProductRepository.findByIdAndFundingStateAndMember(
                fundingId, ONGOING, member);

        if (ObjectUtils.isEmpty(fundingProduct)) {
            throw new FundingException(FUNDING_NOT_FOUND);
        }
        fundingProduct.setFundingState(CANCEL);

        long total = fundingProduct.getTotal();

        Wallet walletInfo = walletRepository.findByMember(member);
        if (total > 0) {
            walletInfo.setBalance(
                walletInfo.getBalance() + total
            );
        }
    }
}
