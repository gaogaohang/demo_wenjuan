package com.couple.platform.controller;

import com.couple.platform.utils.ApiResponse;
import com.couple.platform.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/files")
public class FileController {

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "type", defaultValue = "general") String type) {

        String fileUrl = fileStorageService.storeFile(file, type);

        Map<String, String> result = new HashMap<>();
        result.put("url", fileUrl);
        result.put("filename", file.getOriginalFilename());
        result.put("size", String.valueOf(file.getSize()));

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/upload/avatar")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadAvatar(
            @RequestParam("file") MultipartFile file) {

        String fileUrl = fileStorageService.storeFile(file, "avatars");

        Map<String, String> result = new HashMap<>();
        result.put("avatarUrl", fileUrl);

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/upload/order")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadOrderImage(
            @RequestParam("file") MultipartFile file) {

        String fileUrl = fileStorageService.storeFile(file, "orders");

        Map<String, String> result = new HashMap<>();
        result.put("imageUrl", fileUrl);

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/{type}/**")
    public ResponseEntity<Resource> getFile(@PathVariable String type, @PathVariable String filePath) {
        try {
            String fullPath = type + "/" + filePath;
            byte[] fileContent = fileStorageService.loadFile(fullPath);
            ByteArrayResource resource = new ByteArrayResource(fileContent);

            String contentType = Files.probeContentType(Paths.get(fullPath));
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filePath + "\"")
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
