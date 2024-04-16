package com.soeun.GiftFunding.domain.support;

import com.soeun.GiftFunding.domain.support.entity.SupportDetail;
import com.soeun.GiftFunding.domain.wallet.Wallet;
import com.soeun.GiftFunding.type.SupportState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupportDetailRepository extends JpaRepository<SupportDetail, Long> {

    SupportDetail findByIdAndWalletAndSupportState(
        long id, Wallet wallet, SupportState supportState);

}
