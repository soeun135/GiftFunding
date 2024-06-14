package com.soeun.GiftFunding.repository;

import com.soeun.GiftFunding.entity.SupportDetail;
import com.soeun.GiftFunding.entity.Wallet;
import com.soeun.GiftFunding.type.SupportState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupportDetailRepository extends JpaRepository<SupportDetail, Long> {

    SupportDetail findByIdAndWalletAndSupportState(
        long id, Wallet wallet, SupportState supportState);

}
