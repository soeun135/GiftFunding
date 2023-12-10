package com.soeun.GiftFunding.service;

import static com.soeun.GiftFunding.type.ErrorType.ALREADY_SEND_REQUEST;
import static com.soeun.GiftFunding.type.ErrorType.NOT_ALLOWED_YOURSELF;

import com.soeun.GiftFunding.dto.FriendRequest;
import com.soeun.GiftFunding.dto.FriendRequestList;
import com.soeun.GiftFunding.dto.UserAdapter;
import com.soeun.GiftFunding.entity.Friend;
import com.soeun.GiftFunding.entity.Member;
import com.soeun.GiftFunding.exception.FriendException;
import com.soeun.GiftFunding.repository.FriendRepository;
import com.soeun.GiftFunding.repository.MemberRepository;
import com.soeun.GiftFunding.type.ErrorType;
import com.soeun.GiftFunding.type.FriendState;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {

    private final MemberRepository memberRepository;
    private final FriendRepository friendRepository;

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
            .memberReqId(sendMember)
            .friendState(FriendState.WAIT)
            .build());

        return FriendRequest.Response.builder()
            .email(request.getEmail())
            .message("님에게 친구요청을 보냈습니다.")
            .build();
    }

    private void validateRequest(Member sendMember, Member receiveMember) {
        if (Objects.equals(sendMember.getEmail(), receiveMember.getEmail())) {
            throw new FriendException(NOT_ALLOWED_YOURSELF);
        }

        Optional<Friend> friendOptional = friendRepository.findByMemberReqId(sendMember);

        if (ObjectUtils.isEmpty(friendOptional)) {
            return;
        }
        friendOptional.ifPresent(friend -> {
            if (friend.getMember().getId()
                .equals(receiveMember.getId())) {
                throw new FriendException(ALREADY_SEND_REQUEST);
            }
        });
    }

    @Override
    public Page<FriendRequestList> requestList(UserAdapter userAdapter, Pageable pageable) {
        Member member = memberRepository.findByEmail(userAdapter.getUsername())
            .orElseThrow(() -> new FriendException(ErrorType.USER_NOT_FOUND));

        return friendRepository.findByMember(member, pageable)
            .map(friend -> {
                    if (friend.getFriendState().equals(FriendState.WAIT)) {
                        friend.toDto();
                    }
                    return null;
                });
    }
}