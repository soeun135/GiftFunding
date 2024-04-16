package com.soeun.GiftFunding.repository;

import com.soeun.GiftFunding.domain.fundingProduct.entity.FundingProduct;
import com.soeun.GiftFunding.domain.member.entity.Member;
import com.soeun.GiftFunding.mail.MailReceiverInterface;
import com.soeun.GiftFunding.type.FundingState;
import io.lettuce.core.dynamic.annotation.Param;
import java.sql.Timestamp;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FundingProductRepository extends JpaRepository<FundingProduct, Long> {

    @Query(value = "select * from funding_product where "
        + "funding_state != 'DELIVERY_SUCCESS';",
    nativeQuery = true)
    List<FundingProduct> findByMember(Member Member);

    Page<FundingProduct> findByMemberAndFundingState(Member Member, FundingState friendState,
        Pageable pageable);

    @Query(value = "select m.email , m.name, f.id "
        + "from member m , funding_product f "
        + "where m.id = ( "
        + "select member_id "
        + "from funding_product "
        + "where datediff(expired_at, curdate()) = 3"
        + ");",
        nativeQuery = true)
    List<MailReceiverInterface> findByExpiredThreeDay();

    @Modifying
    @Query(value = "update funding_product f "
        + "set f.funding_state = 'FAIL' "
        + "where datediff(expired_at, curdate()) = 0 "
        + "and "
        + "f.total < ("
        + "select p.price "
        + "from product p "
        + "where p.id = f.product_id);", nativeQuery = true)
    void updateByExpiredWithFail();

    @Query(value = "select * "
        + "from funding_product "
        + "where funding_state = 'FAIL';",
        nativeQuery = true)
    List<FundingProduct> findByFailFunding();

    @Query(value = "select * from funding_product where funding_state = 'SUCCESS' "
        + "and ABS(TIMESTAMPDIFF(second, :send_at, updated_at)) <= 3600;",
        nativeQuery = true)
    List<FundingProduct> findBySuccessFunding(
        @Param("send_at") Timestamp send_at);

    FundingProduct findByIdAndFundingStateAndMember(
        Long id, FundingState fundingState, Member member);
}
