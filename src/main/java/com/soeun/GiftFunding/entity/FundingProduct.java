package com.soeun.GiftFunding.entity;

import com.soeun.GiftFunding.entity.Member;
import com.soeun.GiftFunding.entity.Product;
import com.soeun.GiftFunding.dto.FundingProductDto;
import com.soeun.GiftFunding.type.FundingState;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @LastModifiedDate
    private LocalDateTime updatedAt;

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
