package com.soeun.GiftFunding.repository;

import com.soeun.GiftFunding.entity.Friend;
import com.soeun.GiftFunding.entity.Member;
import com.soeun.GiftFunding.type.FriendState;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
    Page<Friend> findByMemberAndFriendState(Member member, FriendState friendState, Pageable pageable);

    Optional<Friend> findByFriendStateAndMemberRequest(FriendState friendState, Member sendMember);

    boolean existsByMemberRequestAndMember(Member member, Member friend);

    Optional<Friend> findByMemberRequestAndMember(Member sendMember, Member receiveMember);
}
