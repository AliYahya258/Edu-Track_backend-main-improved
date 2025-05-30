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
    private CourseRepository CourseRepository;

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    private AnnouncementFileRepository announcementFileRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private SectionRepository sectionRepository;

    public Announcement createAnnouncement(AnnouncementRequest request,Teacher teacher) {
        Announcement announcement = new Announcement();
        announcement.setTitle(request.getTitle());
        announcement.setContent(request.getContent());

        announcement.setTeacher(teacher);


        Course course = CourseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + request.getCourseId()));

        Optional<Section> sectionOpt = sectionRepository.findBySectionNameAndCourse(request.getSectionName(), course);

        // Fetch section from DB
        //Section section = sectionRepository.findBySectionNameAndCourse(request.getSectionName())
        //.orElseThrow(() -> new RuntimeException("Section not found"));
        if(sectionOpt.isPresent()) {
            Section section = sectionOpt.get();
            announcement.setSection(section);
        }
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

//    public List<AnnouncementResponse> getAnnouncementsBySection(Long sectionId) {
//        log.info("Getting announcements for section ID: {}", sectionId);
//        List<Announcement> announcements = announcementRepository.findBySectionId(sectionId);
//        log.info("Found {} announcements for section ID: {}", announcements.size(), sectionId);
//
//        return announcements.stream().map(this::convertToAnnouncementResponse).toList();
//    }

    public List<AnnouncementResponse> getAnnouncementsByCourseAndSection(Long courseId, String sectionName) {
        Course course = CourseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + courseId));

        Optional<Section> sectionOpt = sectionRepository.findBySectionNameAndCourse(sectionName, course);

        if (sectionOpt.isEmpty()) {
            return new ArrayList<>();
        }

        Section section = sectionOpt.get();
        List<Announcement> announcements = announcementRepository.findBySectionSectionName(section.getSectionName());

        return announcements.stream().map(this::convertToAnnouncementResponse).toList();
    }

    private AnnouncementResponse convertToAnnouncementResponse(Announcement announcement) {
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

    public List<AnnouncementResponse> getAnnouncementsBySection(String sectionName) {
        List<Announcement> announcements = announcementRepository.findBySectionSectionName(sectionName);

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
            if (!announcement.getSection().getSectionName().equals(request.getSectionName())) {
                Section section = sectionRepository.findBySectionName(request.getSectionName())
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