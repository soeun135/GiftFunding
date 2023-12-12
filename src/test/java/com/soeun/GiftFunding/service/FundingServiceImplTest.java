package com.soeun.GiftFunding.service;

import static com.soeun.GiftFunding.type.FundingState.ONGOING;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.soeun.GiftFunding.dto.UserAdapter;
import com.soeun.GiftFunding.entity.FundingProduct;
import com.soeun.GiftFunding.entity.Member;
import com.soeun.GiftFunding.entity.Product;
import com.soeun.GiftFunding.repository.FundingProductRepository;
import com.soeun.GiftFunding.repository.MemberRepository;
import com.soeun.GiftFunding.repository.ProductRepository;
import com.soeun.GiftFunding.repository.WalletRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
@Transactional
class FundingServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private FundingProductRepository fundingProductRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private UserAdapter userAdapter;

    @Mock
    private MemberServiceImpl memberService;
    @InjectMocks
    private FundingServiceImpl fundingService;

    @Test
    @DisplayName("펀딩상품 등록테스트")
    void register() {
        //given
        Product product = Product.builder()
            .id(1L)
            .productName("아동용 장갑")
            .price(1000L)
            .ranking(10)
            .build();

        Member member = Member.builder()
            .id(1L)
            .name("버니")
            .phone("010-1111-1111")
            .email("bunny@naver.com")
            .password("1234")
            .address("경기도 성남시")
            .birthDay(LocalDate.of(2013, 7, 31))
            .createdAt(LocalDateTime.now())
            .build();
        given(productRepository.findById(anyLong()))
            .willReturn(Optional.of(product));

        given(memberRepository.findById(anyLong()))
            .willReturn(Optional.of(member));

        given(fundingProductRepository.save(any()))
            .willReturn(
                FundingProduct.builder()
                    .product(product)
                    .member(member)
                    .total(0L)
                    .fundingState(ONGOING)
                    .build()
            );
        UserDetails userDetails =
            memberService.loadUserByUsername("bunny@naver.com");

        ArgumentCaptor<FundingProduct> fundingCaptor =
            ArgumentCaptor.forClass(FundingProduct.class);
        //when
        fundingService.register(1L, (UserAdapter) userDetails);

        //then
        verify(fundingProductRepository, times(1))
            .save(fundingCaptor.capture());
    }

    @Test
    void cancel() {
        //given
        //when
        //then
    }
}