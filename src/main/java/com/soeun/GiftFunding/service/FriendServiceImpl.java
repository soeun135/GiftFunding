package com.soeun.GiftFunding.service;

import static com.soeun.GiftFunding.type.ErrorType.ALREADY_SEND_REQUEST;
import static com.soeun.GiftFunding.type.ErrorType.NOT_ALLOWED_YOURSELF;
import static com.soeun.GiftFunding.type.ErrorType.REQUEST_NOT_FOUND;
import static com.soeun.GiftFunding.type.ErrorType.USER_NOT_FOUND;
import static com.soeun.GiftFunding.type.FriendState.ACCEPT;

import com.soeun.GiftFunding.dto.FriendRequest;
import com.soeun.GiftFunding.dto.FriendRequestList;
import com.soeun.GiftFunding.dto.FriendRequestProcess;
import com.soeun.GiftFunding.dto.FriendRequestProcess.Request;
import com.soeun.GiftFunding.dto.FriendRequestProcess.Response;
import com.soeun.GiftFunding.dto.UserAdapter;
import com.soeun.GiftFunding.entity.Friend;
import com.soeun.GiftFunding.entity.Member;
import com.soeun.GiftFunding.exception.FriendException;
import com.soeun.GiftFunding.repository.FriendRepository;
import com.soeun.GiftFunding.repository.MemberRepository;
import com.soeun.GiftFunding.type.ErrorType;
import com.soeun.GiftFunding.type.FriendState;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {

    private final MemberRepository memberRepository;
    private final FriendRepository friendRepository;

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
                    return friend.toDto();
                }

                return null;
            });
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

        List<Friend> friendRequestList = friendRepository.findByMember(receiveMember)
            .stream()
            .filter(fr -> fr.getFriendState().equals(FriendState.WAIT))
            .filter(fr -> Objects.equals(fr.getMemberReqId().getId(), sendMember.getId()))
            .collect(Collectors.toList());

        if (friendRequestList.size() < 1) {
            throw new FriendException(REQUEST_NOT_FOUND);
        }

        friendRequestList.get(0).setFriendState(request.getState());

        if (ACCEPT.equals(request.getState())) {
            friendRepository.save(
                Friend.builder()
                    .member(sendMember)
                    .memberReqId(receiveMember)
                    .friendState(ACCEPT)
                    .build()
            );
        }
        return FriendRequestProcess.Response.builder()
            .email(sendMember.getEmail())
            .message("님의 친구요청을 업데이트 했습니다.")
            .build();
    }
}