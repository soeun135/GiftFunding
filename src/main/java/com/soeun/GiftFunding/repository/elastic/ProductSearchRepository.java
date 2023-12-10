package com.soeun.GiftFunding.repository.elastic;

import com.soeun.GiftFunding.entity.document.ProductDocument;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, Long> {

    Page<ProductDocument> findByProductName(String productName, Pageable pageable);

}
