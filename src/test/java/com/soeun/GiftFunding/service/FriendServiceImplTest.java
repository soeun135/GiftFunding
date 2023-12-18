package com.soeun.GiftFunding.service;

import static com.soeun.GiftFunding.type.FriendState.ACCEPT;
import static com.soeun.GiftFunding.type.FriendState.WAIT;
import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.soeun.GiftFunding.dto.FriendList;
import com.soeun.GiftFunding.dto.FriendRequest;
import com.soeun.GiftFunding.dto.UserAdapter;
import com.soeun.GiftFunding.entity.Friend;
import com.soeun.GiftFunding.entity.Member;
import com.soeun.GiftFunding.repository.FriendRepository;
import com.soeun.GiftFunding.repository.FundingProductRepository;
import com.soeun.GiftFunding.repository.MemberRepository;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class FriendServiceImplTest {
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private FriendRepository friendRepository;

    @Mock
    private FundingProductRepository fundingProductRepository;

    @Mock
    private UserAdapter userAdapter;

    @Mock
    private Pageable pageable;

    @InjectMocks
    private FriendServiceImpl friendService;

    @BeforeEach
    public void setUserAdapter() {
        this.userAdapter =
            new UserAdapter("bunny@naver.com", "1234");
        this.pageable = Pageable.unpaged();

    }
    @Test
    @DisplayName("친구 요청 테스트")
    void request() {
        //given
         String sendMemberEmail = "bunny@naver.com";
        String receiveMemberEmail = "bucks@naver.com";

        Member sendMember = new Member();
        sendMember.setId(1L);
        sendMember.setEmail(sendMemberEmail);

        Member receiveMember = new Member();
        receiveMember.setId(2L);
        receiveMember.setEmail(receiveMemberEmail);

        FriendRequest.Request friendRequest = new FriendRequest.Request();
        friendRequest.setEmail(receiveMemberEmail);

        // when
        when(memberRepository.findByEmail(sendMemberEmail))
            .thenReturn(Optional.of(sendMember));
        when(memberRepository.findByEmail(receiveMemberEmail))
            .thenReturn(Optional.of(receiveMember));

        FriendRequest.Response result =
            friendService.request(friendRequest, userAdapter);

        // then
        assertNotNull(result);
        assertEquals(receiveMemberEmail, result.getEmail());
        assertEquals("님에게 친구요청을 보냈습니다.", result.getMessage());
        verify(friendRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("친구 요청 목록 조회 테스트")
    void friendRequestList() {
        //given
        Member member = new Member();
        member.setId(1L);
        member.setEmail(userAdapter.getUsername());

        Member requestMember = new Member();
        requestMember.setId(2L);
        requestMember.setEmail("bucks@naver.com");

       Friend f =
           new Friend(1L, member, requestMember, WAIT, now());
       //when
        when(memberRepository.findByEmail(userAdapter.getUsername()))
            .thenReturn(Optional.of(member));
        when(friendRepository.findByMemberAndFriendState(
            member, WAIT, pageable))
            .thenReturn(new PageImpl(Collections.singletonList(f)));

        Page<FriendList> result =
            friendService.friendList(userAdapter, WAIT, pageable);

        //then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("bucks@naver.com", result.getContent().get(0).getMemberEmail());
    }
    @Test
    @DisplayName("친구 목록 조회 테스트")
    void friendList() {
        //given
        Member member = new Member();
        member.setId(1L);
        member.setEmail(userAdapter.getUsername());

        Member requestMember = new Member();
        requestMember.setId(2L);
        requestMember.setEmail("bucks@naver.com");

        Friend f =
            new Friend(1L, member, requestMember, ACCEPT, now());
        //when
        when(memberRepository.findByEmail(userAdapter.getUsername()))
            .thenReturn(Optional.of(member));
        when(friendRepository.findByMemberAndFriendState(
            member, ACCEPT, pageable))
            .thenReturn(new PageImpl(Collections.singletonList(f)));

        Page<FriendList> result =
            friendService.friendList(userAdapter, ACCEPT, pageable);

        //then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("bucks@naver.com", result.getContent().get(0).getMemberEmail());
    }
}