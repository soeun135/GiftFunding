package com.soeun.GiftFunding.dto;

import com.soeun.GiftFunding.entity.Product;
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
public class SaveProduct {
    private String productName;
    private Long price;
    private int ranking;

    public Product toEntity() {
        return Product.builder()
            .productName(this.productName)
            .price(this.price)
            .ranking(this.ranking)
            .build();
    }
}
