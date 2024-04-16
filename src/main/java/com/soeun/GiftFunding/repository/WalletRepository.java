package com.soeun.GiftFunding.repository;

import com.soeun.GiftFunding.domain.member.entity.Member;
import com.soeun.GiftFunding.domain.wallet.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Wallet findByMember(Member member);

}
