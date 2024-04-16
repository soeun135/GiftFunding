package com.soeun.GiftFunding.domain.friend.repository;

import com.soeun.GiftFunding.domain.friend.entity.Friend;
import com.soeun.GiftFunding.domain.chat.Friend.Friend;
import com.soeun.GiftFunding.domain.member.entity.Member;
import com.soeun.GiftFunding.type.FriendState;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
    Page<Friend> findByMemberAndFriendState(
        Member member, FriendState friendState, Pageable pageable);

    Optional<Friend> findByFriendStateAndMemberRequestAndMember(
        FriendState friendState, Member sendMember, Member receiveMember);

    boolean existsByFriendStateAndMemberRequestAndMember(
        FriendState friendState, Member member, Member friend);

    Optional<Friend> findByMemberRequestAndMember(Member sendMember, Member receiveMember);

    @Transactional
    @Modifying
    @Query(value = "delete from friend where friend_state = 'REJECT';"
        , nativeQuery = true)
    void deleteByRejectFriend();
}
