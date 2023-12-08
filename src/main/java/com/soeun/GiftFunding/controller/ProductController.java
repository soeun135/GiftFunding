package com.soeun.GiftFunding.controller;

import com.soeun.GiftFunding.dto.SaveProduct;
import com.soeun.GiftFunding.dto.SearchProduct;
import com.soeun.GiftFunding.dto.SearchProduct.Response;
import com.soeun.GiftFunding.repository.ProductRepository;
import com.soeun.GiftFunding.repository.elastic.ProductSearchRepository;
import com.soeun.GiftFunding.service.ProductService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
@Slf4j
public class ProductController {
    private final ProductService productService;
    @PostMapping("/save")
    public ResponseEntity<Void> save(@RequestBody SaveProduct saveProduct) {
        productService.saveProduct(saveProduct);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/search")
    public ResponseEntity<List<Response>> search(
        @RequestBody SearchProduct.Request request,
        Pageable pageable) {

        return ResponseEntity.ok(
            productService.findByProductName(request, pageable));
    }
}
