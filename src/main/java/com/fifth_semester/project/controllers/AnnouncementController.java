package com.fifth_semester.project.controllers;

import com.fifth_semester.project.dtos.request.AnnouncementRequest;
import com.fifth_semester.project.dtos.response.AnnouncementResponse;
import com.fifth_semester.project.entities.Announcement;
import com.fifth_semester.project.entities.Teacher;
import com.fifth_semester.project.repositories.TeacherRepository;
import com.fifth_semester.project.security.services.UserDetailsImpl;
import com.fifth_semester.project.services.AnnouncementService;
import com.fifth_semester.project.services.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@CrossOrigin(origins = "http://localhost:3000")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private FileStorageService fileStorageService;

    // Create a new announcement
    @PostMapping("/courseId/sectionName")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<AnnouncementResponse> createAnnouncement(@RequestBody AnnouncementRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Teacher teacher = teacherRepository.findByEmail(userDetails.getEmail())
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        Announcement announcement = announcementService.createAnnouncement(request,teacher);

        // Convert to response DTO (you'll need to implement this in your service)
        List<String> fileUrls = announcement.getFiles() != null ?
                announcement.getFiles().stream().map(file -> file.getFileUrl()).toList() :
                List.of();

        AnnouncementResponse response = new AnnouncementResponse(
                announcement.getId(),
                announcement.getContent(),
                announcement.getTitle(),
                announcement.getTimestamp(),
                announcement.getTeacher().getUsername(),
                fileUrls
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Get all announcements for a specific course and section
    @GetMapping("/course/{courseId}/section/{sectionName}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER')")
    public ResponseEntity<List<AnnouncementResponse>> getAnnouncementsByCourseAndSection(
            @PathVariable Long courseId,
            @PathVariable String sectionName) {
        List<AnnouncementResponse> announcements =
                announcementService.getAnnouncementsByCourseAndSection(courseId, sectionName);
        return ResponseEntity.ok(announcements);
    }

    // Get all announcements for a specific section
    @GetMapping("/section/{sectionName}")
    @PreAuthorize("hasRole('TEACHER')or hasRole('STUDENT')")
    public ResponseEntity<List<AnnouncementResponse>> getAnnouncementsBySection(@PathVariable String sectionName) {
        List<AnnouncementResponse> announcements = announcementService.getAnnouncementsBySection(sectionName);
        return ResponseEntity.ok(announcements);
    }

    // Get a specific announcement by ID
    @GetMapping("/{announcementId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<AnnouncementResponse> getAnnouncement(@PathVariable Long announcementId) {
        // Implement this method in your service
        AnnouncementResponse announcement = announcementService.getAnnouncementById(announcementId);
        if (announcement != null) {
            return ResponseEntity.ok(announcement);
        }
        return ResponseEntity.notFound().build();
    }

    // Update an existing announcement
    @PutMapping("/{announcementId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<AnnouncementResponse> updateAnnouncement(
            @PathVariable Long announcementId,
            @RequestBody AnnouncementRequest request) {
        // Implement this method in your service
        AnnouncementResponse updatedAnnouncement = announcementService.updateAnnouncement(announcementId, request);
        if (updatedAnnouncement != null) {
            return ResponseEntity.ok(updatedAnnouncement);
        }
        return ResponseEntity.notFound().build();
    }

    // Delete an announcement
    @DeleteMapping("/{announcementId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Void> deleteAnnouncement(@PathVariable Long announcementId) {
        // Implement this method in your service
        boolean deleted = announcementService.deleteAnnouncement(announcementId);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Upload a file and attach it to an announcement
    @PostMapping("/{announcementId}/upload")
    @PreAuthorize("hasRole('TEACHER')")
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

    // Download a file
    @GetMapping("/files/{fileName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileName) {
        try {
            byte[] fileData = fileStorageService.getFile(fileName);
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(fileData);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Remove a file from an announcement
    @DeleteMapping("/{announcementId}/files/{fileId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Void> removeFile(@PathVariable Long announcementId, @PathVariable Long fileId) {
        // Implement this method in your service
        boolean removed = announcementService.removeFileFromAnnouncement(announcementId, fileId);
        if (removed) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}