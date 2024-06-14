package com.soeun.GiftFunding.repository;

import com.soeun.GiftFunding.entity.Friend;
import com.soeun.GiftFunding.entity.Member;
import com.soeun.GiftFunding.type.FriendState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
