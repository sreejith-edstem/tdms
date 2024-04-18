package com.elasticsearch.tdma.service;

import com.elasticsearch.tdma.model.ImageData;
import com.elasticsearch.tdma.repository.StorageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class StorageService {
    private final StorageRepository storageRepository;
    private final ObjectMapper objectMapper;
    private final RestHighLevelClient client;

    public void saveJsonFile(String index, MultipartFile multipartFile) throws IOException {
        log.info("Uploading json file to index", index);
        String jsonString = new String(multipartFile.getBytes());
        ImageData imageData = objectMapper.readValue(jsonString, ImageData.class);
        IndexRequest request = new IndexRequest(index);
        request.source(jsonString, XContentType.JSON);
        try {
            client.index(request, RequestOptions.DEFAULT);
            storageRepository.save(imageData);
            log.info("Successfully uploaded JSON file to index {}", index);
        } catch (IOException exception) {
            log.error("Failed to upload JSON file to index {}", index, exception);
            throw exception;
        }
    }

    public List<?> searchResume(String indexName, String language, String fromExperienceLevel, String role, String orderBy, String sortOrder, Integer from, Integer size) throws IOException {
        SearchRequest searchRequest = new SearchRequest(indexName);
        String orderByField = getOrderByField(orderBy);

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery("skillsAndTechnologies.programmingLanguages", language))
                .must(QueryBuilders.rangeQuery("skillsAndTechnologies.totalYearsOfExperience").gte(fromExperienceLevel))
                .must(QueryBuilders.matchQuery("skillsAndTechnologies.role", role));

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                .query(boolQueryBuilder)
                .from(from)
                .size(size);

        if (orderBy != null) {
            sourceBuilder.sort(new FieldSortBuilder(orderByField).order(SortOrder.fromString(sortOrder)));
        }

        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        return getSearchResults(searchResponse);
    }

    private String getOrderByField(String orderBy) {
        Map<String, String> orderByMap = new HashMap<>();
        orderByMap.put("fromExperienceLevel", "skillsAndTechnologies.totalYearsOfExperience");
        orderByMap.put("language", "skillsAndTechnologies.programmingLanguages");
        orderByMap.put("role", "skillsAndTechnologies.role");

        return orderByMap.getOrDefault(orderBy, null);
    }

    private List<Map> getSearchResults(SearchResponse searchResponse) {
        List<Map> result = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            result.add(hit.getSourceAsMap());
        }
        return result;
    }
}
