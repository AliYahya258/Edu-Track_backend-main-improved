package com.fifth_semester.project.services;
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

    private final String uploadDir = "uploads/";

    public String storeFile(MultipartFile file) throws IOException {
        // Ensure directory exists
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Generate unique filename
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + fileName);

        // Save file
        Files.write(filePath, file.getBytes());

        return "/api/announcements/files/" + fileName; // Return download URL
    }

    public byte[] getFile(String fileName) throws IOException {
        Path filePath = Paths.get(uploadDir + fileName);
        return Files.readAllBytes(filePath);
    }
}

