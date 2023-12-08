package com.soeun.GiftFunding.repository.elastic;

import com.soeun.GiftFunding.entity.ProductDocument;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, Long> {

    List<ProductDocument> findByProductName(String productName, Pageable pageable);
}
