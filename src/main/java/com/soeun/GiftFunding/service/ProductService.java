package com.soeun.GiftFunding.service;

import com.soeun.GiftFunding.dto.SaveProduct;
import com.soeun.GiftFunding.dto.SearchProduct;
import com.soeun.GiftFunding.dto.SearchProduct.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    void saveProduct(SaveProduct saveProduct);

    /**
     * ES를 통해 상품명으로 검색하는 메소드
     * 파라미터 : 상품명
     *
     */

    Page<Response> findByProductName(SearchProduct.Request request, Pageable pageable);

    Page<Response> getProductList();
}