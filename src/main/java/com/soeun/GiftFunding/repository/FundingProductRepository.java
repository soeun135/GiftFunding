package com.soeun.GiftFunding.repository;

import com.soeun.GiftFunding.entity.Friend;
import com.soeun.GiftFunding.entity.FundingProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FundingProductRepository extends JpaRepository<FundingProduct, Long> {

}
