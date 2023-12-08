package com.soeun.GiftFunding.entity;

import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setting(settingPath = "/elastic/elastic-settings.json")
@Mapping(mappingPath = "/elastic/product-mappings.json")
@Document(indexName = "product")
public class ProductDocument {
    @Id
    private Long id;

    private String productName;
    private Long price;
    private int ranking;

    public static ProductDocument from(Product product) {
        return ProductDocument.builder()
            .id(product.getId())
            .productName(product.getProductName())
            .price(product.getPrice())
            .ranking(product.getRanking())
            .build();
    }
}
