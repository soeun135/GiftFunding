package com.soeun.GiftFunding.service;

import static com.soeun.GiftFunding.type.ErrorType.PRODUCT_NOT_FOUND;
import static com.soeun.GiftFunding.type.FundingState.ONGOING;

import com.soeun.GiftFunding.dto.UserAdapter;
import com.soeun.GiftFunding.entity.FundingProduct;
import com.soeun.GiftFunding.entity.Member;
import com.soeun.GiftFunding.entity.Product;
import com.soeun.GiftFunding.exception.FriendException;
import com.soeun.GiftFunding.exception.FundingException;
import com.soeun.GiftFunding.repository.FundingProductRepository;
import com.soeun.GiftFunding.repository.MemberRepository;
import com.soeun.GiftFunding.repository.ProductRepository;
import com.soeun.GiftFunding.type.ErrorType;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FundingServiceImpl implements FundingService{
    private final ProductRepository productRepository;
    private final FundingProductRepository fundingProductRepository;
    private final MemberRepository memberRepository;

    @Override
    public void register(long id, UserAdapter userAdapter) {
        Product product = productRepository.findById(id)
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
}
