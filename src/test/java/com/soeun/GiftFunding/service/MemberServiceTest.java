package com.soeun.GiftFunding.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.soeun.GiftFunding.dto.Signup.Request;
import com.soeun.GiftFunding.dto.Signup.Response;
import com.soeun.GiftFunding.entity.Member;
import com.soeun.GiftFunding.repository.FundingProductRepository;
import com.soeun.GiftFunding.repository.MemberRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {
    @Mock
    private MemberRepository memberRepository;

    @Mock
    private FundingProductRepository fundingProductRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private MemberServiceImpl memberServiceImpl;

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

        given(memberRepository.save(any()))
            .willReturn(Member
                .builder()
                .name("soni")
                .phone("01011111111")
                .email("test@naver.com")
                .password("1111") //인코딩해서 넣어야함
                .address("경기도")
                .birthDay(LocalDate.parse("2000-01-28"))
                .build());
        //when
        ArgumentCaptor<Member> captor =
            ArgumentCaptor.forClass(Member.class);

        Response response = memberServiceImpl.singUp(request);

        //then
        verify(memberRepository, times(1))
            .save(captor.capture());
        assertEquals(request.getName(),response.getName());
        assertEquals(request.getName(),captor.getValue().getName());
     }

     @Test
     @DisplayName("로그인 성공 테스트")
     void successSignin() {
         //given
         //given()
         //when
         //then
      }
}