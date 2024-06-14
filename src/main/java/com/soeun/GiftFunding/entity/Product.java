package com.soeun.GiftFunding.entity;

import com.soeun.GiftFunding.dto.SearchProduct;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productName;
    private Long price;
    private int ranking;

    public SearchProduct.Response toDto() {
        return SearchProduct.Response.builder()
            .productName(this.productName)
            .price(this.price)
            .ranking(this.ranking)
            .build();
    }
}
