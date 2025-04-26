package com.fifth_semester.project.dtos.request;

public class AnnouncementRequest {
    private String title;
    private String content;
    private String teacherId;
    private Long sectionId;

    public AnnouncementRequest(String title, Long sectionId, String teacherId, String content) {
        this.title = title;
        this.sectionId = sectionId;
        this.teacherId = teacherId;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
