package com.soeun.GiftFunding.service;

import com.soeun.GiftFunding.dto.FriendRequest;
import com.soeun.GiftFunding.dto.MemberAdapter;
import com.soeun.GiftFunding.entity.Friend;
import com.soeun.GiftFunding.entity.Member;
import com.soeun.GiftFunding.exception.FriendException;
import com.soeun.GiftFunding.repository.FriendRepository;
import com.soeun.GiftFunding.repository.FundingProductRepository;
import com.soeun.GiftFunding.repository.MemberRepository;
import com.soeun.GiftFunding.type.ErrorType;
import com.soeun.GiftFunding.type.FriendState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
}