package com.elasticsearch.tdma.service;

import com.elasticsearch.tdma.model.ImageData;
import com.elasticsearch.tdma.repository.StorageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
}
