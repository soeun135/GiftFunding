package com.soeun.GiftFunding.domain.member.service;

import com.soeun.GiftFunding.domain.member.dto.Signup;
import com.soeun.GiftFunding.domain.member.entity.Member;
import com.soeun.GiftFunding.domain.member.repository.MemberRepository;
import com.soeun.GiftFunding.domain.wallet.Wallet;
import com.soeun.GiftFunding.exception.MemberException;
import com.soeun.GiftFunding.repository.FundingProductRepository;
import com.soeun.GiftFunding.repository.WalletRepository;
import com.soeun.GiftFunding.security.TokenProvider;
import com.soeun.GiftFunding.type.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {
    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private FundingProductRepository fundingProductRepository;

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private MemberServiceImpl memberService;

    private Signup.Request getSignupRequest() {
        return Signup.Request.builder()
                .name("소은")
                .phone("010-1111-1111")
                .email("soni@naver.com")
                .password("qwerty")
                .address("서울특별시 강남구")
                .birthDay("2000-01-28")
                .build();
    }
    @Test
    @DisplayName("회원가입 성공 테스트")
    void signUpSuccessTest() {
        //given
        Signup.Request request = getSignupRequest();

        Member member = getMember();

        given(memberRepository.existsByEmail(request.getEmail()))
                .willReturn(false);

        given(passwordEncoder.encode(request.getPassword()))
                .willReturn("Encoding Password");


        ArgumentCaptor<Member> memberCaptor =
                ArgumentCaptor.forClass(Member.class);
        ArgumentCaptor<Wallet> walletCaptor =
                ArgumentCaptor.forClass(Wallet.class);

        //when
        Signup.Response response = memberService.signUp(request);

        //then
        verify(memberRepository, times(1)).existsByEmail(request.getEmail());
        assertEquals(request.getName(), response.getName());
        verify(memberRepository, times(1)).save(memberCaptor.capture());
        assertEquals(request.getName(), memberCaptor.getValue().getName());
        assertEquals(request.getEmail(), memberCaptor.getValue().getEmail());
        assertEquals("Encoding Password", memberCaptor.getValue().getPassword());
        verify(walletRepository, times(1)).save(walletCaptor.capture());
        assertEquals(member.getName(), walletCaptor.getValue().getMember().getName());
    }

    private Member getMember() {
        Member member = Member.builder()
                .id(1L)
                .name("소은")
                .phone("010-1111-1111")
                .email("soni@naver.com")
                .password("Encoding password")
                .address("서울특별시 강남구")
                .birthDay(LocalDate.of(2000, 01, 28))
                .build();
        return member;
    }
    @Test
    @DisplayName("회원가입 실패 테스트 - 중복된 이메일 존재")
    void signUpFailTest_DuplicatedMember() {
        //given
        Signup.Request request = getSignupRequest();

        given(memberRepository.existsByEmail(request.getEmail()))
                .willReturn(true);

        ArgumentCaptor<String> captor =
                ArgumentCaptor.forClass(String.class);
        //when
        MemberException exception = assertThrows(MemberException.class,
                () -> memberService.signUp(request));

        //then
        verify(memberRepository, times(1)).existsByEmail(captor.capture());
        assertEquals(request.getEmail(), captor.getValue());
        assertEquals(ErrorType.USER_DUPLICATED, exception.getErrorCode());
    }


}