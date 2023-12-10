package com.soeun.GiftFunding.controller;

import com.soeun.GiftFunding.dto.SaveProduct;
import com.soeun.GiftFunding.dto.SearchProduct;
import com.soeun.GiftFunding.dto.SearchProduct.Response;
import com.soeun.GiftFunding.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
@Slf4j
public class ProductController {
    private final ProductService productService;
    @PostMapping
    public ResponseEntity<Void> save(@RequestBody SaveProduct saveProduct) {
        productService.saveProduct(saveProduct);
        return ResponseEntity.ok().build();
    }
    @GetMapping
    public ResponseEntity<Page<Response>> getProductList() {

        return ResponseEntity.ok(
            productService.getProductList()
        );
    }
    @PostMapping("/search")
    public ResponseEntity<Page<Response>> search(
        @RequestBody SearchProduct.Request request,
        Pageable pageable) {

        //todo PageRequest
        return ResponseEntity.ok(
            productService.findByProductName(request, pageable));
    }
}
