package com.soeun.GiftFunding.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.soeun.GiftFunding.domain.product.service.ProductServiceImpl;
import com.soeun.GiftFunding.dto.SaveProduct;
import com.soeun.GiftFunding.dto.SearchProduct;
import com.soeun.GiftFunding.domain.product.entity.Product;
import com.soeun.GiftFunding.entity.document.ProductDocument;
import com.soeun.GiftFunding.domain.product.repository.ProductRepository;
import com.soeun.GiftFunding.repository.elastic.ProductSearchRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductSearchRepository productSearchRepository;

    @Mock
    private Pageable pageable;

    @Mock
    private Page page;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void saveProduct() {
        SaveProduct saveProduct =
            SaveProduct.builder()
                .productName("장갑")
                .price(1000L)
                .ranking(1)
                .build();
        given(productRepository.save(any()))
            .willReturn(
                saveProduct.toEntity()
            );
        ArgumentCaptor<Product> captor =
            ArgumentCaptor.forClass(Product.class);
        Product product = productRepository.save(saveProduct.toEntity());

        verify(productRepository, times(1)).save(captor.capture());
        assertEquals(saveProduct.getProductName(), product.getProductName());
    }

    @Test
    void findByProductName() {
        SearchProduct.Request request =
            SearchProduct.Request.builder()
                .productName("장갑")
                .build();
        ProductDocument document =
            ProductDocument.builder()
                .productName("아동용 장갑")
                .price(1000L)
                .ranking(3)
                .build();
        List<ProductDocument> list = new ArrayList<>();
        list.add(document);
        list.add(ProductDocument.builder()
            .productName("가죽 장갑")
            .price(103300L)
            .ranking(1)
            .build());

        Page<ProductDocument> documentPage = new PageImpl<>(list);
        given(productSearchRepository.findByProductName(
            request.getProductName(), pageable))
            .willReturn(page);
        //when
        productService.findByProductName(request, pageable);

        ArgumentCaptor<ProductDocument> captor =
            ArgumentCaptor.forClass(ProductDocument.class);
        //then
        verify(productSearchRepository, times(1)).findByProductName("장갑", pageable);
        assertEquals(2, documentPage.getTotalElements());
    }

    @Test
    void getProductList() {
        //given
        Product p1 =
            Product.builder()
                .productName("아동용 장갑")
                .price(1000L)
                .ranking(1)
                .build();
        Product p2 =
            Product.builder()
                .productName("가죽 장갑")
                .price(50000L)
                .ranking(2)
                .build();
        Product p3 =
            Product.builder()
                .productName("벙어리 장갑")
                .price(5000L)
                .ranking(3)
                .build();
        // 가상의 Product 객체 리스트 생성
        List<Product> mockProducts = Arrays.asList(p1, p2, p3);

        // 가상의 Product DTO 객체 리스트 생성
        List<SearchProduct.Response> expectedDtoList =
            Arrays.asList(p1.toDto(), p2.toDto(), p3.toDto()
            );

        // ProductRepository의 findAll 메소드가 호출될 때 가상의 데이터를 반환하도록 설정
        when(productRepository.findAll(Sort.by("ranking").ascending()))
            .thenReturn(mockProducts);

        // 테스트 대상 메소드 호출
        Page<SearchProduct.Response> result =
            productService.getProductList();

        // 결과 검증
        assertEquals(expectedDtoList.get(0).getProductName(),
            result.getContent().get(0).getProductName());
    }
}