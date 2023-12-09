package com.soeun.GiftFunding.service;

import com.soeun.GiftFunding.dto.SaveProduct;
import com.soeun.GiftFunding.dto.SearchProduct;
import com.soeun.GiftFunding.dto.SearchProduct.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    void saveProduct(SaveProduct saveProduct);

    Page<Response> findByProductName(SearchProduct.Request request, Pageable pageable);

    Page<Response> getProductList();
}