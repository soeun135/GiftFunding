package com.soeun.GiftFunding.entity;

import com.soeun.GiftFunding.dto.FundingProductDto;
import com.soeun.GiftFunding.type.FundingState;
import java.time.LocalDate;
import javax.persistence.Column;
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
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@DynamicUpdate
public class FundingProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    private Long total;

    @CreatedDate
    @Column(updatable = false)
    private LocalDate createdAt;

    private LocalDate expiredAt;

    @Enumerated(EnumType.STRING)
    private FundingState fundingState;

    public FundingProductDto toDto() {
        return FundingProductDto.builder()
            .id(this.id)
            .product(this.product)
            .total(this.total)
            .createdAt(this.createdAt)
            .expiredAt(this.expiredAt)
            .fundingState(this.fundingState)
            .build();
    }
}
