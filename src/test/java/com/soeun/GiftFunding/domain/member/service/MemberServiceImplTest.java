package com.soeun.GiftFunding.domain.member.service;

import com.soeun.GiftFunding.domain.fundingProduct.entity.FundingProduct;
import com.soeun.GiftFunding.domain.member.dto.Signin;
import com.soeun.GiftFunding.domain.member.dto.Signup;
import com.soeun.GiftFunding.domain.member.dto.UpdateInfo;
import com.soeun.GiftFunding.domain.member.dto.UserInfoResponse;
import com.soeun.GiftFunding.domain.member.entity.Member;
import com.soeun.GiftFunding.domain.member.repository.MemberRepository;
import com.soeun.GiftFunding.domain.product.entity.Product;
import com.soeun.GiftFunding.domain.wallet.Wallet;
import com.soeun.GiftFunding.dto.MemberAdapter;
import com.soeun.GiftFunding.dto.ReissueResponse;
import com.soeun.GiftFunding.exception.MemberException;
import com.soeun.GiftFunding.exception.TokenException;
import com.soeun.GiftFunding.redis.RefreshTokenRepository;
import com.soeun.GiftFunding.repository.FundingProductRepository;
import com.soeun.GiftFunding.repository.WalletRepository;
import com.soeun.GiftFunding.security.TokenProvider;
import com.soeun.GiftFunding.type.ErrorType;
import com.soeun.GiftFunding.type.FundingState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    private RefreshTokenRepository refreshTokenRepository;

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

    @Test
    @DisplayName("로그인 성공 테스트")
    void signInSuccessTest() {
        //given
        Signin.Request request = getSignInRequest();

        given(memberRepository.findByEmail(request.getEmail()))
                .willReturn(Optional.ofNullable(getMember()));

        given(passwordEncoder.matches(eq(request.getPassword()), any()))
                .willReturn(true);

        given(tokenProvider.generateAccessToken(request.getEmail()))
                .willReturn("AccessToken");
        given(tokenProvider.generateRefreshToken(request.getEmail()))
                .willReturn("RefreshToken");

        ArgumentCaptor<String> captor =
                ArgumentCaptor.forClass(String.class);
        //when
        Signin.Response response = memberService.signIn(request);

        //then
        assertEquals(response.getAccessToken(), "AccessToken");
        assertEquals(response.getRefreshToken(), "RefreshToken");
        verify(memberRepository, times(1)).findByEmail(captor.capture());
        assertEquals(request.getEmail(), captor.getValue());
    }

    @Test
    @DisplayName("로그인 실패 테스트 - 회원을 찾을 수 없음")
    void signInFailTest_UserNotFound() {
        //given
        Signin.Request request = getSignInRequest();

        given(memberRepository.findByEmail(request.getEmail()))
                .willReturn(Optional.empty());

        //when
        MemberException exception = assertThrows(MemberException.class,
                () -> memberService.signIn(request));

        //then
        assertEquals(ErrorType.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("로그인 실패 테스트 - 비밀번호가 일치하지 않음")
    void signInFailTest_PasswordUnmatched() {
        //given
        Signin.Request request = getSignInRequest();

        given(memberRepository.findByEmail(request.getEmail()))
                .willReturn(Optional.ofNullable(getMember()));

        given(passwordEncoder.matches(eq(request.getPassword()), any()))
                .willReturn(false);

        //when
        MemberException exception = assertThrows(MemberException.class,
                () -> memberService.signIn(request));

        //then
        assertEquals(ErrorType.PASSWORD_UNMATCHED, exception.getErrorCode());
    }

    private Signin.Request getSignInRequest() {
        return new Signin.Request("soni@naver.com", "qwerty");
    }

    @Test
    @DisplayName("토큰 재발급 성공 테스트")
    void reissueSuccessTest() {
        //given
        String request = "Bearer RefreshToken";
        String refreshToken = request.substring("Bearer ".length());

        given(tokenProvider.reIssueAccessToken(refreshToken))
                .willReturn("ReissueAccessToken");

        ArgumentCaptor<String> captor =
                ArgumentCaptor.forClass(String.class);
        //when
        ReissueResponse response = memberService.reissue(request);

        //then
        verify(tokenProvider, times(1)).reIssueAccessToken(captor.capture());
        assertEquals(refreshToken, captor.getValue());
        assertEquals(refreshToken, response.getRefreshToken());
    }

    @Test
    @DisplayName("토큰 재발급 실패 테스트 - RefreshToken 만료")
    void reissueFailTest_RefreshExpired() {
        //given
        String request = "Bearer RefreshToken";
        String refreshToken = request.substring("Bearer ".length());

        given(tokenProvider.reIssueAccessToken(refreshToken))
                .willThrow(new TokenException(ErrorType.REFRESHTOKEN_EXPIRED));

        //when
        TokenException exception = assertThrows(TokenException.class,
                () -> memberService.reissue(request));

        //then
        assertEquals(ErrorType.REFRESHTOKEN_EXPIRED, exception.getErrorCode());
    }

    @Test
    @DisplayName("사용자 정보 조회 성공 테스트")
    void userInfoSuccessTest() {
        //given
        MemberAdapter memberAdapter = getMemberAdapter();

        Member member = getMember();

        given(memberRepository.findByEmail(memberAdapter.getUsername()))
                .willReturn(Optional.of(member));

        given(fundingProductRepository.findByMember(member))
                .willReturn(Arrays.asList(
                        FundingProduct.builder()
                                .id(1L)
                                .product(new Product(1L, "반지", 10000L, 1))
                                .member(member)
                                .total(5000L)
                                .fundingState(FundingState.ONGOING)
                                .build(),
                        FundingProduct.builder()
                                .id(2L)
                                .product(new Product(2L, "목걸이", 20000L, 2))
                                .member(member)
                                .total(1000L)
                                .fundingState(FundingState.ONGOING)
                                .build()
                ));

        ArgumentCaptor<String> captor =
                ArgumentCaptor.forClass(String.class);

        //when
        UserInfoResponse response =
                memberService.userInfo(memberAdapter);

        //then
        verify(memberRepository, times(1)).findByEmail(captor.capture());
        assertEquals(memberAdapter.getUsername(), captor.getValue());
        assertEquals(member.getName(), response.getName());
        assertEquals(member.getPhone(), response.getPhone());
        assertEquals(member.getEmail(), response.getEmail());
        assertEquals(member.getAddress(), response.getAddress());
        assertEquals("반지", response.getFundingProductList().get(0).getProduct().getProductName());
        assertEquals("목걸이", response.getFundingProductList().get(1).getProduct().getProductName());
    }

    @Test
    @DisplayName("사용자 정보 조회 실패 테스트 - 사용자를 찾을 수 없음")
    void userInfoFailTest_UserNotFound() {
        //given
        MemberAdapter memberAdapter = getMemberAdapter();

        given(memberRepository.findByEmail(memberAdapter.getUsername()))
                .willReturn(Optional.empty());

        //when
        MemberException exception = assertThrows(MemberException.class,
                () -> memberService.userInfo(memberAdapter));

        //then
        assertEquals(ErrorType.USER_NOT_FOUND, exception.getErrorCode());
    }

    private MemberAdapter getMemberAdapter() {
        MemberAdapter memberAdapter = new MemberAdapter("soni@naver.com", "qwerty");
        return memberAdapter;
    }

    @Test
    @DisplayName("사용자 정보 수정 성공 테스트")
    void updateSuccessTest() {
        //given
        UpdateInfo.Request request =
                new UpdateInfo.Request(
                        "버니", "010-1111-1111", "서울특별시 강남구", LocalDate.of(2000, 01, 28));

        MemberAdapter memberAdapter = getMemberAdapter();

        given(memberRepository.findByEmail(memberAdapter.getUsername()))
                .willReturn(Optional.of(getMember()));

        ArgumentCaptor<String> captor =
                ArgumentCaptor.forClass(String.class);

        //when
        UpdateInfo.Response response =
                memberService.update(request, memberAdapter);

        //then
        verify(memberRepository, times(1)).findByEmail(captor.capture());
        assertEquals(memberAdapter.getUsername(), captor.getValue());
        assertEquals(request.getName(), response.getName());
        assertEquals(memberAdapter.getUsername(), response.getEmail());
        assertEquals(request.getPhone(), response.getPhone());
        assertEquals(request.getAddress(), response.getAddress());
        assertEquals(request.getBirthDay(), response.getBirthDay());
    }

    @Test
    @DisplayName("사용자 정보 수정 성공 테스트 - 이름만 변경할 때")
    void updateSuccessTest_UpdateName() {
        //given
        UpdateInfo.Request request =
                new UpdateInfo.Request(
                        "버니", "", "", null);

        MemberAdapter memberAdapter = getMemberAdapter();
        Member member = getMember();

        given(memberRepository.findByEmail(memberAdapter.getUsername()))
                .willReturn(Optional.of(member));

        //when
        UpdateInfo.Response response =
                memberService.update(request, memberAdapter);

        //then
        assertEquals(request.getName(), response.getName());

        assertEquals(memberAdapter.getUsername(), response.getEmail());

        assertEquals(member.getPhone(), response.getPhone());
        assertNotEquals(request.getPhone(), response.getPhone());

        assertEquals(member.getAddress(), response.getAddress());
        assertNotEquals(request.getAddress(), response.getAddress());

        assertEquals(member.getBirthDay(), response.getBirthDay());
        assertNotEquals(request.getBirthDay(), response.getBirthDay());
    }

    @Test
    @DisplayName("사용자 정보 수정 성공 테스트 - 전화번호만 변경할 때")
    void updateSuccessTest_UpdatePhone() {
        //given
        UpdateInfo.Request request =
                new UpdateInfo.Request(
                        "", "010-2222-2222", "", null);

        MemberAdapter memberAdapter = getMemberAdapter();
        Member member = getMember();

        given(memberRepository.findByEmail(memberAdapter.getUsername()))
                .willReturn(Optional.of(member));

        //when
        UpdateInfo.Response response =
                memberService.update(request, memberAdapter);

        //then
        assertEquals(member.getName(), response.getName());
        assertNotEquals(request.getName(), response.getName());

        assertEquals(memberAdapter.getUsername(), response.getEmail());

        assertEquals(request.getPhone(), response.getPhone());

        assertEquals(member.getAddress(), response.getAddress());
        assertNotEquals(request.getAddress(), response.getAddress());

        assertEquals(member.getBirthDay(), response.getBirthDay());
        assertNotEquals(request.getBirthDay(), response.getBirthDay());
    }

    @Test
    @DisplayName("사용자 정보 수정 성공 테스트 - 주소만 변경할 때")
    void updateSuccessTest_UpdateAddress() {
        //given
        UpdateInfo.Request request =
                new UpdateInfo.Request(
                        "", "", "경기도 성남시", null);

        MemberAdapter memberAdapter = getMemberAdapter();
        Member member = getMember();

        given(memberRepository.findByEmail(memberAdapter.getUsername()))
                .willReturn(Optional.of(member));

        //when
        UpdateInfo.Response response =
                memberService.update(request, memberAdapter);

        //then
        assertEquals(member.getName(), response.getName());
        assertNotEquals(request.getName(), response.getName());

        assertEquals(memberAdapter.getUsername(), response.getEmail());

        assertEquals(member.getPhone(), response.getPhone());
        assertNotEquals(request.getPhone(), response.getPhone());

        assertEquals(request.getAddress(), response.getAddress());

        assertEquals(member.getBirthDay(), response.getBirthDay());
        assertNotEquals(request.getBirthDay(), response.getBirthDay());
    }

    @Test
    @DisplayName("사용자 정보 수정 성공 테스트 - 생일만 변경할 때")
    void updateSuccessTest_UpdateBirthDay() {
        //given
        UpdateInfo.Request request =
                new UpdateInfo.Request(
                        "", "", "", LocalDate.of(2024, 04, 20));

        MemberAdapter memberAdapter = getMemberAdapter();
        Member member = getMember();

        given(memberRepository.findByEmail(memberAdapter.getUsername()))
                .willReturn(Optional.of(member));

        //when
        UpdateInfo.Response response =
                memberService.update(request, memberAdapter);

        //then
        assertEquals(member.getName(), response.getName());
        assertNotEquals(request.getName(), response.getName());

        assertEquals(memberAdapter.getUsername(), response.getEmail());

        assertEquals(member.getPhone(), response.getPhone());
        assertNotEquals(request.getPhone(), response.getPhone());

        assertEquals(member.getAddress(), response.getAddress());
        assertNotEquals(request.getAddress(), response.getAddress());

        assertEquals(request.getBirthDay(), response.getBirthDay());
    }

    @Test
    @DisplayName("사용자 정보 수정 실패 테스트 - 사용자를 찾을 수 없음")
    void updateFailTest_UserNotFound() {
        //given
        UpdateInfo.Request request =
                new UpdateInfo.Request(
                        "버니", "010-1111-1111", "서울특별시 강남구", LocalDate.of(2000, 01, 28));

        MemberAdapter memberAdapter = getMemberAdapter();

        given(memberRepository.findByEmail(memberAdapter.getUsername()))
                .willReturn(Optional.empty());


        //when
        MemberException exception = assertThrows(MemberException.class,
                () -> memberService.update(request, memberAdapter));

        //then
        assertEquals(ErrorType.USER_NOT_FOUND, exception.getErrorCode());
    }
}