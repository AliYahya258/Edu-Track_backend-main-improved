package com.fifth_semester.project.controllers;

import com.fifth_semester.project.services.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fifth_semester.project.services.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/{announcementId}/upload")
    public ResponseEntity<String> uploadFile(@PathVariable Long announcementId,
                                             @RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = fileStorageService.storeFile(file);
            announcementService.attachFileToAnnouncement(announcementId, file.getOriginalFilename(), fileUrl);
            return ResponseEntity.ok("File uploaded successfully: " + fileUrl);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed");
        }
    }

    @GetMapping("/files/{fileName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileName) {
        try {
            byte[] fileData = fileStorageService.getFile(fileName);
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(fileData);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }
}


