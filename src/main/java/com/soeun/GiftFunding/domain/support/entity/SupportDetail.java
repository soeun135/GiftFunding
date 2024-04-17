package com.soeun.GiftFunding.domain.support.entity;

import com.soeun.GiftFunding.domain.fundingProduct.entity.FundingProduct;
import com.soeun.GiftFunding.domain.wallet.Wallet;
import com.soeun.GiftFunding.type.SupportState;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@DynamicUpdate
public class SupportDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "funding_product_id")
    private FundingProduct fundingProduct;

    @ManyToOne
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    private Long supportAmount;

    @Enumerated(EnumType.STRING)
    private SupportState supportState;

    @CreatedDate
    private LocalDateTime createdAt;
}
