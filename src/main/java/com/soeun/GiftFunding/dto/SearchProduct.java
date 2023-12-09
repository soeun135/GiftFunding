package com.soeun.GiftFunding.dto;

import com.soeun.GiftFunding.entity.document.ProductDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


public class SearchProduct {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private String productName;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private String productName;
        private Long price;
        private int ranking;

        public static Response from(ProductDocument document) {
            return Response.builder()
                .productName(document.getProductName())
                .price(document.getPrice())
                .ranking(document.getRanking())
                .build();
        }
    }
}
