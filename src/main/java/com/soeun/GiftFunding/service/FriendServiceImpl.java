package com.soeun.GiftFunding.service;

import static com.soeun.GiftFunding.type.ErrorType.ALREADY_FRIEND_MEMBER;
import static com.soeun.GiftFunding.type.ErrorType.ALREADY_RECEIVE_FRIEND_REQUEST;
import static com.soeun.GiftFunding.type.ErrorType.ALREADY_SEND_REQUEST;
import static com.soeun.GiftFunding.type.ErrorType.FRIEND_INFO_NOT_FOUND;
import static com.soeun.GiftFunding.type.ErrorType.NOT_ALLOWED_YOURSELF;
import static com.soeun.GiftFunding.type.ErrorType.REQUEST_NOT_FOUND;
import static com.soeun.GiftFunding.type.ErrorType.USER_NOT_FOUND;
import static com.soeun.GiftFunding.type.FriendState.ACCEPT;
import static com.soeun.GiftFunding.type.FriendState.WAIT;
import static com.soeun.GiftFunding.type.FundingState.ONGOING;

import com.soeun.GiftFunding.dto.FriendFundingProduct;
import com.soeun.GiftFunding.dto.FriendList;
import com.soeun.GiftFunding.dto.FriendRequest;
import com.soeun.GiftFunding.dto.FriendRequestProcess;
import com.soeun.GiftFunding.dto.FriendRequestProcess.Request;
import com.soeun.GiftFunding.dto.FriendRequestProcess.Response;
import com.soeun.GiftFunding.dto.UserAdapter;
import com.soeun.GiftFunding.entity.Friend;
import com.soeun.GiftFunding.entity.FundingProduct;
import com.soeun.GiftFunding.entity.Member;
import com.soeun.GiftFunding.exception.FriendException;
import com.soeun.GiftFunding.repository.FriendRepository;
import com.soeun.GiftFunding.repository.FundingProductRepository;
import com.soeun.GiftFunding.repository.MemberRepository;
import com.soeun.GiftFunding.type.ErrorType;
import com.soeun.GiftFunding.type.FriendState;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {

    private final MemberRepository memberRepository;
    private final FriendRepository friendRepository;
    private final FundingProductRepository fundingProductRepository;

    //todo 로그인한 사용자 에러처리 필요하다면 private 메소드 하나로 빼기
    @Override
    public FriendRequest.Response request(
        FriendRequest.Request request, UserAdapter userAdapter) {
        //친구 요청을 보내는 사용자
        Member sendMember = memberRepository.findByEmail(userAdapter.getUsername())
            .orElseThrow(() -> new FriendException(ErrorType.USER_NOT_FOUND));

        //친구 요청을 받는 사용자
        Member receiveMember = memberRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new FriendException(ErrorType.USER_NOT_FOUND));

        validateRequest(sendMember, receiveMember);

        friendRepository.save(Friend.builder()
            .member(receiveMember)
            .memberRequest(sendMember)
            .friendState(FriendState.WAIT)
            .build());

        return FriendRequest.Response.builder()
            .email(request.getEmail())
            .message("님에게 친구요청을 보냈습니다.")
            .build();
    }

    private void validateRequest(Member sendMember, Member receiveMember) {
        if (Objects.equals(sendMember.getId(), receiveMember.getId())) {
            throw new FriendException(NOT_ALLOWED_YOURSELF);
        }

        Optional<Friend> sendFriendOptional =
            friendRepository.findByMemberRequestAndMember(
                sendMember, receiveMember
            );

        sendFriendOptional.ifPresent(friend -> {
            if (friend.getFriendState() == WAIT) {
                throw new FriendException(ALREADY_SEND_REQUEST);
            } else if (friend.getFriendState() == ACCEPT) {
                throw new FriendException(ALREADY_FRIEND_MEMBER);
            }
        });

        Optional<Friend> receiveFriendOptional =
            friendRepository.findByMemberRequestAndMember(
                receiveMember, sendMember
            );

        receiveFriendOptional.ifPresent(friend -> {
            throw new FriendException(ALREADY_RECEIVE_FRIEND_REQUEST);
        });
    }

    @Override
    public Page<FriendList> friendList(
        UserAdapter userAdapter,
        FriendState friendState,
        Pageable pageable) {
        Member member = memberRepository.findByEmail(userAdapter.getUsername())
            .orElseThrow(() -> new FriendException(ErrorType.USER_NOT_FOUND));

        return friendRepository.findByMemberAndFriendState(
                member, friendState, pageable)
            .map(Friend::toFriendReqDto);
    }

    @Override
    @Transactional
    public Response requestProcess(UserAdapter userAdapter, Request request) {
        //초기 친구요청을 받은 사용자 == 로그인한 사용자
        Member receiveMember = memberRepository.findByEmail(userAdapter.getUsername())
            .orElseThrow(() -> new FriendException(USER_NOT_FOUND));

        //친구 요청을 보낸 사용자
        Member sendMember = memberRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new FriendException(USER_NOT_FOUND));


        Friend friend =
            friendRepository.findByFriendStateAndMemberRequestAndMember(
                    WAIT, sendMember, receiveMember)
                .orElseThrow(() -> new FriendException(REQUEST_NOT_FOUND));

        friend.setFriendState(request.getState());
        friend.setCreatedAt(LocalDateTime.now());

        if (ACCEPT.equals(request.getState())) {
            friendRepository.save(
                Friend.builder()
                    .member(sendMember)
                    .memberRequest(receiveMember)
                    .friendState(ACCEPT)
                    .createdAt(friend.getCreatedAt())
                    .build()
            );
        }
        return FriendRequestProcess.Response.builder()
            .email(sendMember.getEmail())
            .message("님의 친구요청 상태를 업데이트 했습니다.")
            .build();
    }

    @Override
    public FriendFundingProduct friendProduct(
        UserAdapter userAdapter, Long id, Pageable pageable) {

        Member member = memberRepository.findByEmail(userAdapter.getUsername())
            .orElseThrow(() -> new FriendException(ErrorType.USER_NOT_FOUND));

        Member friend = memberRepository.findById(id)
            .orElseThrow(() -> new FriendException(USER_NOT_FOUND));

        if (!friendRepository.existsByFriendStateAndMemberRequestAndMember(
            ACCEPT, member, friend)) {
            throw new FriendException(FRIEND_INFO_NOT_FOUND);
        }
        return FriendFundingProduct.builder()
            .name(friend.getName())
            .phone(friend.getPhone())
            .email(friend.getEmail())
            .birthDay(friend.getBirthDay())
            .fundingProductList(fundingProductRepository.findByMemberAndFundingState(
                    friend, ONGOING, pageable)
                .map(FundingProduct::toDto))
            .build();
    }
}