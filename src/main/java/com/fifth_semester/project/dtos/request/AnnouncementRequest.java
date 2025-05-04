package com.fifth_semester.project.dtos.request;

public class AnnouncementRequest {
    private String title;
    private String content;
    private String sectionName;
    private Long courseId;


    public AnnouncementRequest(String title, String sectionName, String content, Long courseId) {
        this.title = title;
        this.sectionName = sectionName;
        this.content = content;
        this.courseId = courseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
}
