package com.soeun.GiftFunding.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.soeun.GiftFunding.dto.Signup.Request;
import com.soeun.GiftFunding.dto.Signup.Response;
import com.soeun.GiftFunding.dto.UserInfoResponse;
import com.soeun.GiftFunding.entity.FundingProduct;
import com.soeun.GiftFunding.entity.Product;
import com.soeun.GiftFunding.entity.User;
import com.soeun.GiftFunding.repository.FundingProductRepository;
import com.soeun.GiftFunding.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private FundingProductRepository fundingProductRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private UserServiceImpl userServiceimpl;

    @Test
    void successSignup() {
        //given
        Request request = Request.builder()
            .name("soni")
            .phone("01011111111")
            .email("test@naver.com")
            .password("1111") //인코딩해서 넣어야함
            .address("경기도")
            .birthDay("2000-01-28")
            .build();

        given(userRepository.save(any()))
            .willReturn(User
                .builder()
                .name("soni")
                .phone("01011111111")
                .email("test@naver.com")
                .password("1111") //인코딩해서 넣어야함
                .address("경기도")
                .birthDay(LocalDate.parse("2000-01-28"))
                .build());
        //when
        ArgumentCaptor<User> captor =
            ArgumentCaptor.forClass(User.class);

        Response response = userServiceimpl.singUp(request);

        //then
        verify(userRepository, times(1))
            .save(captor.capture());
        assertEquals(request.getName(),response.getName());
        assertEquals(request.getName(),captor.getValue().getName());

     }

/*
     @Test
     void SuccessUserInfo() {
         User user = User.builder()
             .name("soni")
             .phone("01011111111")
             .email("test@naver.com")
             .password("1111") //인코딩해서 넣어야함
             .address("경기도")
             .birthDay(LocalDate.parse("2000-01-28"))
             .build();

         Product product = Product.builder()
             .productName("아이폰")
             .price(100000L)
             .ranking(1)
             .build();
         FundingProduct fundingProduct = FundingProduct.builder()
             .product(product)
             .user(user)
             .total(10000L)
             .createdAt(LocalDateTime.now())
             .expiredAt(LocalDateTime.now().plusYears(1))
             .build();
         //given
         given(userServiceimpl.userInfo(anyString()))
             .willReturn(UserInfoResponse.builder()
                 .name(user.getName())
                 .phone(user.getPhone())
                 .email(user.getEmail())
                 .address(user.getAddress())
                 .birthDay(user.getBirthDay())
                 .fundingProductList(
                     fundingProductRepository.findByUserId(user.getId()))
                 .build());

         //when
         UserInfoResponse response = userServiceimpl.userInfo("Bearer abcde");

         //then
         assertEquals(response.getName(), user.getName());
      }

      @Test
      void b() {
          //given
          //when
          //then
       }
       */
}