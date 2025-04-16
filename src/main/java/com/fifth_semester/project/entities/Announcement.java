package com.fifth_semester.project.entities;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;


@Entity
public class Announcement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;
    private LocalDateTime timestamp;

    @ManyToOne
    private Teacher teacher;  // The teacher who posted it

    @ManyToOne
    private Section section;  // The section it's posted in

    @OneToMany(mappedBy = "announcement", cascade = CascadeType.ALL)
    private List<AnnouncementFile> files;  // Attached files

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<AnnouncementFile> getFiles() {
        return files;
    }

    public void setFiles(List<AnnouncementFile> files) {
        this.files = files;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

