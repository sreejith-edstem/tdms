package com.elasticsearch.tdma.repository;

import com.elasticsearch.tdma.model.ImageData;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StorageRepository extends ElasticsearchRepository<ImageData,String> {
}
