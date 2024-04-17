package com.soeun.GiftFunding.dto;

import com.soeun.GiftFunding.domain.product.entity.Product;
import com.soeun.GiftFunding.type.FundingState;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FundingProductDto {
    private Long id;
    private Product product;
    private Long total;
    private LocalDate createdAt;
    private LocalDate expiredAt;
    private FundingState fundingState;

}
