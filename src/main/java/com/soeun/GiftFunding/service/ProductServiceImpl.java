package com.soeun.GiftFunding.service;

import com.soeun.GiftFunding.dto.SaveProduct;
import com.soeun.GiftFunding.dto.SearchProduct;
import com.soeun.GiftFunding.dto.SearchProduct.Response;
import com.soeun.GiftFunding.entity.Product;
import com.soeun.GiftFunding.entity.document.ProductDocument;
import com.soeun.GiftFunding.repository.ProductRepository;
import com.soeun.GiftFunding.repository.elastic.ProductSearchRepository;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductSearchRepository productSearchRepository;

    public void saveProduct(SaveProduct saveProduct) {
        Product productEntity = productRepository.save(saveProduct.toEntity());

        productSearchRepository.save(
            ProductDocument.from(productEntity));
    }

    public Page<Response> findByProductName(
        SearchProduct.Request request, Pageable pageable) {

        return productSearchRepository.findByProductName(request.getProductName(), pageable)
            .map(Response::from);
    }

    @Override
    public Page<Response> getProductList() {

        return new PageImpl<>(
            productRepository.findAll(
                    Sort.by("ranking").ascending())
                .stream()
                .map(Product::toDto)
                .collect(Collectors.toList()));
    }
}
