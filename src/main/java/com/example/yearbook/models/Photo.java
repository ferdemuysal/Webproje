package com.example.yearbook.models;

public class Photo {
    private int id;
    private String filePath;
    private int userId;
    private String uploaderName;
    private boolean isProfilePicture;
    
    
    public Photo() {
    }

    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUploaderName() {
        return uploaderName;
    }

    public void setUploaderName(String uploaderName) {
        this.uploaderName = uploaderName;
    }
    public boolean isProfilePicture() {
        return isProfilePicture;
    }

    public void setProfilePicture(boolean profilePicture) {
        isProfilePicture = profilePicture;
    }
}