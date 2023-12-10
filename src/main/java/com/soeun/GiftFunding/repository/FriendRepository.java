package com.soeun.GiftFunding.repository;

import com.soeun.GiftFunding.entity.Friend;
import com.soeun.GiftFunding.entity.Member;
import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
    Optional<Friend> findByMemberReqId(Member sendMember);

    Page<Friend> findByMember(Member member, Pageable pageable);
}
