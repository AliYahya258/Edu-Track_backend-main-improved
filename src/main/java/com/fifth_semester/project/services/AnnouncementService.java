package com.fifth_semester.project.services;
import com.fifth_semester.project.entities.*;
import com.fifth_semester.project.repositories.*;
import com.fifth_semester.project.dtos.response.*;
import com.fifth_semester.project.dtos.request.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
            List<String> fileUrls = a.getFiles().stream().map(AnnouncementFile::getFileUrl).toList();
            return new AnnouncementResponse(a.getId(), a.getTitle(), a.getContent(), a.getTimestamp(),
                    a.getTeacher().getUsername(), fileUrls);
        }).toList();
    }
}


