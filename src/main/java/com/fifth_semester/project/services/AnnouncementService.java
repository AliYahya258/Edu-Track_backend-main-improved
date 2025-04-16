package com.fifth_semester.project.services;
import com.fifth_semester.project.entities.*;
import com.fifth_semester.project.repositories.*;
import com.fifth_semester.project.dtos.response.*;
import com.fifth_semester.project.dtos.request.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AnnouncementService {

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    private AnnouncementFileRepository announcementFileRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private SectionRepository sectionRepository;

    public Announcement createAnnouncement(AnnouncementRequest request) {
        Announcement announcement = new Announcement();
        announcement.setTitle(request.getTitle());
        announcement.setContent(request.getContent());

        // Fetch teacher from DB
        Teacher teacher = teacherRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        announcement.setTeacher(teacher);

        // Fetch section from DB
        Section section = sectionRepository.findById(request.getSectionId())
                .orElseThrow(() -> new RuntimeException("Section not found"));
        announcement.setSection(section);

        announcement.setTimestamp(LocalDateTime.now());

        return announcementRepository.save(announcement);
    }

    public void attachFileToAnnouncement(Long announcementId, String fileName, String fileUrl) {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new RuntimeException("Announcement not found"));

        AnnouncementFile file = new AnnouncementFile();
        file.setFileName(fileName);
        file.setFileUrl(fileUrl);
        file.setAnnouncement(announcement);

        announcementFileRepository.save(file);
    }

    public List<AnnouncementResponse> getAnnouncementsBySection(Long sectionId) {
        List<Announcement> announcements = announcementRepository.findBySectionId(sectionId);

        return announcements.stream().map(a -> {
            List<String> fileUrls = a.getFiles() != null ?
                    a.getFiles().stream().map(AnnouncementFile::getFileUrl).toList() :
                    new ArrayList<>();
            return new AnnouncementResponse(a.getId(), a.getContent(), a.getTitle(), a.getTimestamp(),
                    a.getTeacher().getUsername(), fileUrls);
        }).toList();
    }

    // NEW METHODS

    public AnnouncementResponse getAnnouncementById(Long announcementId) {
        Optional<Announcement> announcementOpt = announcementRepository.findById(announcementId);

        if (announcementOpt.isPresent()) {
            Announcement announcement = announcementOpt.get();
            List<String> fileUrls = announcement.getFiles() != null ?
                    announcement.getFiles().stream().map(AnnouncementFile::getFileUrl).toList() :
                    new ArrayList<>();

            return new AnnouncementResponse(
                    announcement.getId(),
                    announcement.getContent(),
                    announcement.getTitle(),
                    announcement.getTimestamp(),
                    announcement.getTeacher().getUsername(),
                    fileUrls
            );
        }

        return null;
    }

    public AnnouncementResponse updateAnnouncement(Long announcementId, AnnouncementRequest request) {
        Optional<Announcement> announcementOpt = announcementRepository.findById(announcementId);

        if (announcementOpt.isPresent()) {
            Announcement announcement = announcementOpt.get();

            // Update fields
            announcement.setTitle(request.getTitle());
            announcement.setContent(request.getContent());

            // Update section if needed
            if (!announcement.getSection().getId().equals(request.getSectionId())) {
                Section section = sectionRepository.findById(request.getSectionId())
                        .orElseThrow(() -> new RuntimeException("Section not found"));
                announcement.setSection(section);
            }

            // Save the updated announcement
            Announcement updatedAnnouncement = announcementRepository.save(announcement);

            // Convert to response DTO
            List<String> fileUrls = updatedAnnouncement.getFiles() != null ?
                    updatedAnnouncement.getFiles().stream().map(AnnouncementFile::getFileUrl).toList() :
                    new ArrayList<>();

            return new AnnouncementResponse(
                    updatedAnnouncement.getId(),
                    updatedAnnouncement.getContent(),
                    updatedAnnouncement.getTitle(),
                    updatedAnnouncement.getTimestamp(),
                    updatedAnnouncement.getTeacher().getUsername(),
                    fileUrls
            );
        }

        return null;
    }

    public boolean deleteAnnouncement(Long announcementId) {
        if (announcementRepository.existsById(announcementId)) {
            announcementRepository.deleteById(announcementId);
            return true;
        }
        return false;
    }

    public boolean removeFileFromAnnouncement(Long announcementId, Long fileId) {
        // First verify the announcement exists
        if (!announcementRepository.existsById(announcementId)) {
            return false;
        }

        // Find the file and check if it belongs to the announcement
        Optional<AnnouncementFile> fileOpt = announcementFileRepository.findById(fileId);
        if (fileOpt.isPresent() && fileOpt.get().getAnnouncement().getId().equals(announcementId)) {
            announcementFileRepository.deleteById(fileId);
            return true;
        }

        return false;
    }
}