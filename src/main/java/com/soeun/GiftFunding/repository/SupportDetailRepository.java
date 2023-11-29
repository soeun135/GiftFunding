package com.soeun.GiftFunding.repository;

import com.soeun.GiftFunding.entity.SupportDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupportDetailRepository extends JpaRepository<SupportDetail, Long> {

}
