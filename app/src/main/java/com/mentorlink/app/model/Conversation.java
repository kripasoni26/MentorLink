package com.mentorlink.app.model;

public class Conversation {
    private String conversationId;
    private String mentorId;
    private String mentorName;
    private String mentorImageUrl;
    private String studentId;
    private String studentName;
    private String studentImageUrl;
    private String lastMessage;
    private long lastUpdated;

    public Conversation() {
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getMentorId() {
        return mentorId;
    }

    public void setMentorId(String mentorId) {
        this.mentorId = mentorId;
    }

    public String getMentorName() {
        return mentorName;
    }

    public void setMentorName(String mentorName) {
        this.mentorName = mentorName;
    }

    public String getMentorImageUrl() {
        return mentorImageUrl;
    }

    public void setMentorImageUrl(String mentorImageUrl) {
        this.mentorImageUrl = mentorImageUrl;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentImageUrl() {
        return studentImageUrl;
    }

    public void setStudentImageUrl(String studentImageUrl) {
        this.studentImageUrl = studentImageUrl;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
