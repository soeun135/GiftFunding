package com.soeun.GiftFunding.repository;

import com.soeun.GiftFunding.entity.FundingProduct;
import com.soeun.GiftFunding.entity.Member;
import com.soeun.GiftFunding.type.FundingState;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FundingProductRepository extends JpaRepository<FundingProduct, Long> {
    List<FundingProduct> findByMember(Member Member);
    Page<FundingProduct> findByMemberAndFundingState(Member Member, FundingState friendState, Pageable pageable);
}
