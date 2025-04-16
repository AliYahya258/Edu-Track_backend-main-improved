package com.fifth_semester.project.dtos.response;

import java.time.LocalDateTime;
import java.util.List;

public class AnnouncementResponse {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime timestamp;
    private String teacherName;
    private List<String> fileUrls;

    public AnnouncementResponse(Long id, String content, String title, LocalDateTime timestamp, String teacherName, List<String> fileUrls) {
        this.id = id;
        this.content = content;
        this.title = title;
        this.timestamp = timestamp;
        this.teacherName = teacherName;
        this.fileUrls = fileUrls;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public List<String> getFileUrls() {
        return fileUrls;
    }

    public void setFileUrls(List<String> fileUrls) {
        this.fileUrls = fileUrls;
    }
}

