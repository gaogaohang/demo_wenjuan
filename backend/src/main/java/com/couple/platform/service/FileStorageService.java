package com.couple.platform.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${couple.file.upload-path}")
    private String uploadPath;

    @Value("${couple.file.access-url-prefix}")
    private String accessUrlPrefix;

    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadPath));
        } catch (IOException e) {
            throw new RuntimeException("无法创建上传目录", e);
        }
    }

    public String storeFile(MultipartFile file, String subPath) {
        try {
            init();

            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = UUID.randomUUID().toString() + extension;

            Path targetPath = Paths.get(uploadPath, subPath);
            Files.createDirectories(targetPath);

            Path filePath = targetPath.resolve(newFilename);
            Files.copy(file.getInputStream(), filePath);

            return accessUrlPrefix + "/" + subPath + "/" + newFilename;
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败", e);
        }
    }

    public String storeFile(MultipartFile file) {
        return storeFile(file, "");
    }

    public byte[] loadFile(String filePath) throws IOException {
        Path path = Paths.get(uploadPath).resolve(filePath);
        return Files.readAllBytes(path);
    }

    public boolean deleteFile(String fileUrl) {
        try {
            String relativePath = fileUrl.replace(accessUrlPrefix, "").substring(1);
            Path path = Paths.get(uploadPath).resolve(relativePath);
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            return false;
        }
    }
}
