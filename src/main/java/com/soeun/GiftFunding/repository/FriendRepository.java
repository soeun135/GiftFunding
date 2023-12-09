package com.soeun.GiftFunding.repository;

import com.soeun.GiftFunding.entity.Friend;
import com.soeun.GiftFunding.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
    Optional<Friend> findByMemberReqId(Member sendMember);
}
