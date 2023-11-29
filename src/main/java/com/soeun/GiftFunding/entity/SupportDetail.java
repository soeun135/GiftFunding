package com.soeun.GiftFunding.entity;

import com.soeun.GiftFunding.type.SupportState;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
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
