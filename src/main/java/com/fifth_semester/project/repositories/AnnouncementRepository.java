package com.fifth_semester.project.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import com.fifth_semester.project.entities.Announcement;
import java.util.List;


public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findBySectionSectionName(String sectionName);
}

