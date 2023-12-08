package com.soeun.GiftFunding.service;

import com.soeun.GiftFunding.dto.SaveProduct;
import com.soeun.GiftFunding.dto.SearchProduct;
import com.soeun.GiftFunding.dto.SearchProduct.Response;
import com.soeun.GiftFunding.entity.Product;
import com.soeun.GiftFunding.entity.ProductDocument;
import com.soeun.GiftFunding.repository.ProductRepository;
import com.soeun.GiftFunding.repository.elastic.ProductSearchRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductSearchRepository productSearchRepository;

    public void saveProduct(SaveProduct saveProduct) {
        Product productEntity = productRepository.save(saveProduct.toEntity());

        productSearchRepository.save(
            ProductDocument.from(productEntity));
    }

    public List<SearchProduct.Response> findByProductName(
        SearchProduct.Request request, Pageable pageable) {
        return productSearchRepository.findByProductName(
                request.getProductName(), pageable)
            .stream()
            .map(Response::from)
            .collect(Collectors.toList());
    }
}
