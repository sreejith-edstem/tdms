package com.elasticsearch.tdma.controller;

import com.elasticsearch.tdma.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class StorageController {
    private final StorageService storageService;

    @PostMapping("/upload")
    public void saveJsonFile(@RequestParam(value = "file") MultipartFile file) throws IOException {
        storageService.saveJsonFile("json", file);
    }

    @GetMapping("/search")
    public List<?> searchResume(@RequestParam String indexName,
                                @RequestParam String language,
                                @RequestParam String fromExperienceLevel,
                                @RequestParam String role,
                                @RequestParam(required = false) String orderBy,
                                @RequestParam(defaultValue = "asc") String sortOrder,
                                @RequestParam(defaultValue = "0") Integer from,
                                @RequestParam(defaultValue = "10") Integer size) {
        try {
            return storageService.searchResume(indexName, language, fromExperienceLevel, role, orderBy, sortOrder, from, size);
        } catch (IOException e) {
            throw new RuntimeException("Error occurred while searching resumes", e);
        }
    }

}
