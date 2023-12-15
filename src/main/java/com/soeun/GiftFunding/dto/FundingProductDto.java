package com.soeun.GiftFunding.dto;

import com.soeun.GiftFunding.entity.Product;
import com.soeun.GiftFunding.type.FundingState;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
