package com.soeun.GiftFunding.service;

import com.soeun.GiftFunding.domain.friend.dto.FriendFundingProduct;
import com.soeun.GiftFunding.domain.friend.dto.FriendList;
import com.soeun.GiftFunding.domain.friend.dto.FriendRequest;
import com.soeun.GiftFunding.domain.friend.dto.FriendRequestProcess;
import com.soeun.GiftFunding.domain.friend.service.FriendServiceImpl;
import com.soeun.GiftFunding.dto.*;
import com.soeun.GiftFunding.domain.chat.Friend.Friend;
import com.soeun.GiftFunding.domain.fundingProduct.entity.FundingProduct;
import com.soeun.GiftFunding.domain.member.entity.Member;
import com.soeun.GiftFunding.domain.product.entity.Product;
import com.soeun.GiftFunding.exception.FriendException;
import com.soeun.GiftFunding.domain.friend.repository.FriendRepository;
import com.soeun.GiftFunding.repository.FundingProductRepository;
import com.soeun.GiftFunding.domain.member.repository.MemberRepository;
import com.soeun.GiftFunding.type.ErrorType;
import com.soeun.GiftFunding.type.FriendState;
import com.soeun.GiftFunding.type.FundingState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.soeun.GiftFunding.type.ErrorType.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FriendServiceImplTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private FriendRepository friendRepository;

    @Mock
    private FundingProductRepository fundingProductRepository;

    @InjectMocks
    private FriendServiceImpl friendService;

    private MemberAdapter memberAdapter1() {
        return new MemberAdapter("soni@naver.com", "qwerty");
    }

    private MemberAdapter memberAdapter2() {
        return new MemberAdapter("buni@naver.com", "qwerty");
    }

    private Member member1() {
        return Member.builder()
                .id(1L)
                .name("소은")
                .phone("010-1111-1111")
                .email("soni@naver.com")
                .password("qwerty")
                .address("서울특별시 강남구")
                .birthDay(LocalDate.of(2000, 01, 28))
                .build();
    }

    private Member member2() {
        return Member.builder()
                .id(2L)
                .name("버니")
                .phone("010-2222-2222")
                .email("buni@naver.com")
                .password("qwerty")
                .address("서울특별시 강남구")
                .birthDay(LocalDate.of(2000, 01, 28))
                .build();
    }

    private Member member3() {
        return Member.builder()
                .id(3L)
                .name("벅스")
                .phone("010-3333-3333")
                .email("bucks@naver.com")
                .password("qwerty")
                .address("서울특별시 강남구")
                .birthDay(LocalDate.of(2000, 01, 28))
                .build();
    }

    @Test
    @DisplayName("친구 요청 성공 테스트")
    void friendRequestSuccessTest() {
        //given
        FriendRequest.Request request =
                new FriendRequest.Request("buni@naver.com");

        //요청 보내는 사용자 member1 받는 사용자 member2
        given(memberRepository.findByEmail(memberAdapter1().getUsername()))
                .willReturn(Optional.of(member1()));

        given(memberRepository.findByEmail(request.getEmail()))
                .willReturn(Optional.of(member2()));

        ArgumentCaptor<Friend> captor =
                ArgumentCaptor.forClass(Friend.class);

        //when
        FriendRequest.Response response =
                friendService.request(request, memberAdapter1());

        //then
        verify(memberRepository, times(2)).findByEmail(anyString());
        verify(friendRepository, times(1)).save(captor.capture());
        assertEquals(member1().getEmail(), captor.getValue().getMemberRequest().getEmail());
        assertEquals(member2().getEmail(), captor.getValue().getMember().getEmail());
        assertEquals(FriendState.WAIT, captor.getValue().getFriendState());
    }

    @Test
    @DisplayName("친구 요청 실패 테스트 - 요청 건 사용자를 찾을 수 없음")
    void friendRequestFailTest_RequestMemberNotFound() {
        //given
        FriendRequest.Request request =
                new FriendRequest.Request("buni@naver.com");

        //요청 보내는 사용자 member1 받는 사용자 member2
        given(memberRepository.findByEmail(memberAdapter1().getUsername()))
                .willReturn(Optional.empty());

        //when
        FriendException friendException = assertThrows(
                FriendException.class,
                () -> friendService.request(request, memberAdapter1()));

        //then
        assertEquals(ErrorType.USER_NOT_FOUND, friendException.getErrorCode());
    }

    @Test
    @DisplayName("친구 요청 실패 테스트 - 요청 대상 사용자를 찾을 수 없음")
    void friendRequestFailTest_MemberNotFound() {
        //given
        FriendRequest.Request request =
                new FriendRequest.Request("buni@naver.com");

        //요청 보내는 사용자 member1 받는 사용자 member2
        given(memberRepository.findByEmail(memberAdapter1().getUsername()))
                .willReturn(Optional.of(member1()));

        given(memberRepository.findByEmail(request.getEmail()))
                .willReturn(Optional.empty());

        //when
        FriendException friendException = assertThrows(
                FriendException.class,
                () -> friendService.request(request, memberAdapter1()));

        //then
        assertEquals(ErrorType.USER_NOT_FOUND, friendException.getErrorCode());
    }

    @Test
    @DisplayName("친구 요청 실패 테스트 - 자기자신에게 요청을 걸 수 없음")
    void friendRequestFailTest_NotAllowedYourSelf() {
        //given
        FriendRequest.Request request =
                new FriendRequest.Request("buni@naver.com");

        //요청 보내는 사용자 member1 받는 사용자 member2
        given(memberRepository.findByEmail(memberAdapter1().getUsername()))
                .willReturn(Optional.of(member1()));

        given(memberRepository.findByEmail(request.getEmail()))
                .willReturn(Optional.of(member1()));

        ArgumentCaptor<Friend> captor =
                ArgumentCaptor.forClass(Friend.class);

        //when
        FriendException exception =
                assertThrows(FriendException.class,
                        () -> friendService.request(request, memberAdapter1()));

        //then
        assertEquals(ErrorType.NOT_ALLOWED_YOURSELF, exception.getErrorCode());
    }

    @Test
    @DisplayName("친구 요청 실패 테스트 - 이미 요청 건 상대에게 요청 걸 수 없음")
    void friendRequestFailTest_AlreadySendRequest() {
        //given
        FriendRequest.Request request =
                new FriendRequest.Request("buni@naver.com");

        Member member1 = member1();
        Member member2 = member2();
        //요청 보내는 사용자 member1 받는 사용자 member2
        given(memberRepository.findByEmail(memberAdapter1().getUsername()))
                .willReturn(Optional.of(member1));

        given(memberRepository.findByEmail(request.getEmail()))
                .willReturn(Optional.of(member2));

        given(friendRepository.findByMemberRequestAndMember(member1, member2))
                .willReturn(Optional.of(Friend.builder()
                        .id(1L)
                        .member(member2)
                        .memberRequest(member1)
                        .friendState(FriendState.WAIT)
                        .build()));

        //when
        FriendException exception = assertThrows(FriendException.class,
                () -> friendService.request(request, memberAdapter1()));

        //then
        assertEquals(ErrorType.ALREADY_SEND_REQUEST, exception.getErrorCode());
    }

    @Test
    @DisplayName("친구 요청 실패 테스트 - 이미 친구인 상대에게 요청 걸 수 없음")
    void friendRequestFailTest_AlreadyFriendMember() {
        //given
        FriendRequest.Request request =
                new FriendRequest.Request("buni@naver.com");

        Member member1 = member1();
        Member member2 = member2();
        //요청 보내는 사용자 member1 받는 사용자 member2
        given(memberRepository.findByEmail(memberAdapter1().getUsername()))
                .willReturn(Optional.of(member1));

        given(memberRepository.findByEmail(request.getEmail()))
                .willReturn(Optional.of(member2));

        given(friendRepository.findByMemberRequestAndMember(member1, member2))
                .willReturn(Optional.of(Friend.builder()
                        .id(1L)
                        .member(member2)
                        .memberRequest(member1)
                        .friendState(FriendState.ACCEPT)
                        .build()));

        //when
        FriendException exception = assertThrows(FriendException.class,
                () -> friendService.request(request, memberAdapter1()));

        //then
        assertEquals(ErrorType.ALREADY_FRIEND_MEMBER, exception.getErrorCode());
    }

    @Test
    @DisplayName("친구 요청 실패 테스트 - 이미 요청이 받은 상대에게 요청 걸 수 없음")
    void friendRequestFailTest_AlreadyReceiveFriend() {
        //given
        FriendRequest.Request request =
                new FriendRequest.Request("buni@naver.com");

        Member member1 = member1();
        Member member2 = member2();
        //요청 보내는 사용자 member1 받는 사용자 member2
        given(memberRepository.findByEmail(memberAdapter1().getUsername()))
                .willReturn(Optional.of(member1));

        given(memberRepository.findByEmail(request.getEmail()))
                .willReturn(Optional.of(member2));

        given(friendRepository.findByMemberRequestAndMember(member1, member2))
                .willReturn(Optional.empty());

        given(friendRepository.findByMemberRequestAndMember(member2, member1))
                .willReturn(Optional.of(Friend.builder()
                        .id(1L)
                        .member(member1)
                        .memberRequest(member2)
                        .friendState(FriendState.WAIT)
                        .build()));

        //when
        FriendException exception = assertThrows(FriendException.class,
                () -> friendService.request(request, memberAdapter1()));

        //then
        assertEquals(ErrorType.ALREADY_RECEIVE_FRIEND_REQUEST, exception.getErrorCode());
    }

    @Test
    @DisplayName("친구 목록 조회 성공 테스트 - ACCEPT")
    void friendListSuccessTest_ACCEPT() {
        //given
        Member member1 = member1();
        Member member2 = member2();
        Member member3 = member3();

        Pageable pageable = PageRequest.of(0, 10);

        given(memberRepository.findByEmail(memberAdapter1().getUsername()))
                .willReturn(Optional.of(member1));

        List<Friend> friendList =
                Arrays.asList(
                        Friend.builder()
                                .id(1L)
                                .member(member1)
                                .memberRequest(member2)
                                .friendState(FriendState.ACCEPT)
                                .build(),
                        Friend.builder()
                                .id(1L)
                                .member(member1)
                                .memberRequest(member3)
                                .friendState(FriendState.ACCEPT)
                                .build());

        given(friendRepository.findByMemberAndFriendState(
                member1, FriendState.ACCEPT, pageable))
                .willReturn(new PageImpl(friendList));

        //when
        Page<FriendList> resultFriendList = friendService.friendList(
                memberAdapter1(), FriendState.ACCEPT, pageable);

        //then
        assertEquals(2, resultFriendList.getSize());
        assertEquals("buni@naver.com", resultFriendList.getContent().get(0).getMemberEmail());
        assertEquals("bucks@naver.com", resultFriendList.getContent().get(1).getMemberEmail());
        verify(memberRepository, times(1)).findByEmail(member1.getEmail());
        verify(friendRepository, times(1)).findByMemberAndFriendState(member1, FriendState.ACCEPT, pageable);
        verify(friendRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("친구 목록 조회 성공 테스트 - WAIT")
    void friendListSuccessTest_WAIT() {
        //given
        Member member1 = member1();
        Member member2 = member2();
        Member member3 = member3();

        Pageable pageable = PageRequest.of(0, 10);

        given(memberRepository.findByEmail(memberAdapter1().getUsername()))
                .willReturn(Optional.of(member1));

        List<Friend> friendList =
                Arrays.asList(
                        Friend.builder()
                                .id(1L)
                                .member(member1)
                                .memberRequest(member2)
                                .friendState(FriendState.WAIT)
                                .build(),
                        Friend.builder()
                                .id(2L)
                                .member(member1)
                                .memberRequest(member3)
                                .friendState(FriendState.WAIT)
                                .build());

        given(friendRepository.findByMemberAndFriendState(
                member1, FriendState.WAIT, pageable))
                .willReturn(new PageImpl(friendList));

        ArgumentCaptor<String> captor =
                ArgumentCaptor.forClass(String.class);

        //when
        Page<FriendList> resultFriendList = friendService.friendList(
                memberAdapter1(), FriendState.WAIT, pageable);

        //then
        assertEquals(2, resultFriendList.getSize());
        verify(memberRepository, times(1)).findByEmail(captor.capture());
        assertEquals(memberAdapter1().getUsername(), captor.getValue());
        assertEquals("buni@naver.com", resultFriendList.getContent().get(0).getMemberEmail());
        assertEquals("bucks@naver.com", resultFriendList.getContent().get(1).getMemberEmail());
        verify(friendRepository, times(1)).findByMemberAndFriendState(member1, FriendState.WAIT, pageable);
        verify(friendRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("친구 목록 조회 실패 테스트 - 로그인 한 사용자를 찾을 수 없음")
    void friendListFailTest_UserNotFound() {
        //given
        Member member1 = member1();

        Pageable pageable = PageRequest.of(0, 10);

        given(memberRepository.findByEmail(memberAdapter1().getUsername()))
                .willReturn(Optional.empty());

        //when
        FriendException exception =
                assertThrows(FriendException.class,
                        () -> friendService.friendList(
                                memberAdapter1(), FriendState.ACCEPT, pageable));

        //then
        assertEquals(ErrorType.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("요청 처리 성공 테스트 - ACCEPT")
    void requestProcessSuccessTest_ACCEPT() {
        //given
        FriendRequestProcess.Request request =
                new FriendRequestProcess.Request(
                        "buni@naver.com", FriendState.ACCEPT);

        Member sender = member2();
        Member receiver = member1();

        //로그인한 사용자 member1 == 초기 요청을 받은 사용자
        given(memberRepository.findByEmail(memberAdapter1().getUsername()))
                .willReturn(Optional.of(receiver));

        given(memberRepository.findByEmail(request.getEmail()))
                .willReturn(Optional.of(sender));

        given(friendRepository.findByFriendStateAndMemberRequestAndMember(
                FriendState.WAIT, sender, receiver))
                .willReturn(Optional.of(
                        Friend.builder()
                                .id(1L)
                                .member(receiver)
                                .memberRequest(sender)
                                .friendState(FriendState.WAIT)
                                .build()
                ));

        ArgumentCaptor<Friend> captor =
                ArgumentCaptor.forClass(Friend.class);

        //when
        FriendRequestProcess.Response response =
                friendService.requestProcess(memberAdapter1(), request);

        //then
        assertEquals(sender.getEmail(), response.getEmail());
        verify(friendRepository, times(1)).save(captor.capture());
        assertEquals(sender, captor.getValue().getMember());
        assertEquals(receiver, captor.getValue().getMemberRequest());
    }

    @Test
    @DisplayName("요청 처리 성공 테스트 - REJECT")
    void requestProcessSuccessTest_REJECT() {
        //given
        FriendRequestProcess.Request request =
                new FriendRequestProcess.Request(
                        "buni@naver.com", FriendState.REJECT);

        Member sender = member2();
        Member receiver = member1();

        //로그인한 사용자 member1 == 초기 요청을 받은 사용자
        given(memberRepository.findByEmail(memberAdapter1().getUsername()))
                .willReturn(Optional.of(receiver));

        given(memberRepository.findByEmail(request.getEmail()))
                .willReturn(Optional.of(sender));

        given(friendRepository.findByFriendStateAndMemberRequestAndMember(
                FriendState.WAIT, sender, receiver))
                .willReturn(Optional.of(
                        Friend.builder()
                                .id(1L)
                                .member(receiver)
                                .memberRequest(sender)
                                .friendState(FriendState.WAIT)
                                .build()
                ));

        //when
        FriendRequestProcess.Response response =
                friendService.requestProcess(memberAdapter1(), request);

        //then
        assertEquals(sender.getEmail(), response.getEmail());
        verify(friendRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("요청처리 실패 테스트 - 로그인 한 사용자를 찾지 못 함")
    void requestProcessFailTest_ReceiveUserNotFound() {
        //given
        FriendRequestProcess.Request request =
                new FriendRequestProcess.Request(
                        "buni@naver.com", FriendState.REJECT);


        //로그인한 사용자 member1 == 초기 요청을 받은 사용자
        given(memberRepository.findByEmail(memberAdapter1().getUsername()))
                .willReturn(Optional.empty());

        //when
        FriendException exception = assertThrows(FriendException.class,
                () -> friendService.requestProcess(
                        memberAdapter1(), request));

        //then
        assertEquals(USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("요청처리 실패 테스트 - 요청 보낸 사용자를 찾지 못 함")
    void requestProcessFailTest_SendUserNotFound() {
        //given
        FriendRequestProcess.Request request =
                new FriendRequestProcess.Request(
                        "buni@naver.com", FriendState.REJECT);

        //로그인한 사용자 member1 == 초기 요청을 받은 사용자
        given(memberRepository.findByEmail(memberAdapter1().getUsername()))
                .willReturn(Optional.of(member1()));

        given(memberRepository.findByEmail(memberAdapter1().getUsername()))
                .willReturn(Optional.empty());

        //when
        FriendException exception = assertThrows(FriendException.class,
                () -> friendService.requestProcess(
                        memberAdapter1(), request));

        //then
        assertEquals(USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("요청처리 실패 테스트 - 요청을 찾지 못 함")
    void requestProcessFailTest_RequestNotFound() {
        //given
        FriendRequestProcess.Request request =
                new FriendRequestProcess.Request(
                        "buni@naver.com", FriendState.REJECT);

        Member sender = member2();
        Member receiver = member1();

        //로그인한 사용자 member1 == 초기 요청을 받은 사용자
        given(memberRepository.findByEmail(memberAdapter1().getUsername()))
                .willReturn(Optional.of(receiver));

        given(memberRepository.findByEmail(request.getEmail()))
                .willReturn(Optional.of(sender));

        given(friendRepository.findByFriendStateAndMemberRequestAndMember(
                FriendState.WAIT, sender, receiver))
                .willReturn(Optional.empty());

        //when
        FriendException exception = assertThrows(FriendException.class,
                () -> friendService.requestProcess(
                        memberAdapter1(), request));

        //then
        assertEquals(REQUEST_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("친구의 펀딩상품 조회 성공 테스트")
    void friendProductSuccessTest() {
        //given
        Pageable pageable = PageRequest.of(0, 10);
        Long friendId = 1L;

        Member member = member1();
        Member friend = member2();

        given(memberRepository.findByEmail(memberAdapter1().getUsername()))
                .willReturn(Optional.of(member));

        given(memberRepository.findById(friendId))
                .willReturn(Optional.of(friend));

        given(friendRepository.existsByFriendStateAndMemberRequestAndMember(
                FriendState.ACCEPT, member, friend))
                .willReturn(true);

        List<FundingProduct> fundingProductList =
                Arrays.asList(
                        FundingProduct.builder()
                                .id(1L)
                                .product(new Product(1L, "반지", 10000L, 1))
                                .member(friend)
                                .total(5000L)
                                .fundingState(FundingState.ONGOING)
                                .build(),
                        FundingProduct.builder()
                                .id(2L)
                                .product(new Product(2L, "목걸이", 20000L, 2))
                                .member(friend)
                                .total(1000L)
                                .fundingState(FundingState.ONGOING)
                                .build()
                );

        given(fundingProductRepository.findByMemberAndFundingState(
                friend, FundingState.ONGOING, pageable))
                .willReturn(new PageImpl(fundingProductList));

        //when
        FriendFundingProduct response =
                friendService.friendProduct(
                        memberAdapter1(), friendId, pageable);

        //then
        assertEquals(member2().getEmail(), response.getEmail());
        assertEquals("반지", response.getFundingProductList().getContent().get(0).getProduct().getProductName());
        verify(fundingProductRepository, times(1)).findByMemberAndFundingState(friend, FundingState.ONGOING, pageable);
    }

    @Test
    @DisplayName("친구의 펀딩상품 조회 실패 테스트")
    void friendProductFailTest_UserNotFound() {
        //given
        Pageable pageable = PageRequest.of(0, 10);
        Long friendId = 1L;

        Member member = member1();
        Member friend = member2();

        given(memberRepository.findByEmail(memberAdapter1().getUsername()))
                .willReturn(Optional.empty());

        //when
        FriendException exception = assertThrows(FriendException.class,
                () -> friendService.friendProduct(memberAdapter1(), friendId, pageable));
        //then
        assertEquals(USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("친구의 펀딩상품 조회 실패 테스트 - 친구를 찾을 수 없음")
    void friendProductFailTest_FriendUserNotFound() {
        //given
        Pageable pageable = PageRequest.of(0, 10);
        Long friendId = 1L;

        Member member = member1();
        Member friend = member2();

        given(memberRepository.findByEmail(memberAdapter1().getUsername()))
                .willReturn(Optional.of(member));

        given(memberRepository.findById(friendId))
                .willReturn(Optional.empty());

        //when
        FriendException exception = assertThrows(FriendException.class,
                () -> friendService.friendProduct(
                        memberAdapter1(), friendId, pageable));

        //then
        assertEquals(USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("친구의 펀딩상품 조회 실패 테스트 - 친구가 아닌 사용자에 대해 조회")
    void friendProductFailTest_FriendInfoNotFound() {
        //given
        Pageable pageable = PageRequest.of(0, 10);
        Long friendId = 1L;

        Member member = member1();
        Member friend = member2();

        given(memberRepository.findByEmail(memberAdapter1().getUsername()))
                .willReturn(Optional.of(member));

        given(memberRepository.findById(friendId))
                .willReturn(Optional.of(friend));

        given(friendRepository.existsByFriendStateAndMemberRequestAndMember(
                FriendState.ACCEPT, member, friend))
                .willReturn(false);

        //when
        FriendException exception = assertThrows(FriendException.class,
                () -> friendService.friendProduct(
                        memberAdapter1(), friendId, pageable));

        //then
        assertEquals(FRIEND_INFO_NOT_FOUND, exception.getErrorCode());
    }
}