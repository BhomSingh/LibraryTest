package com.chat.befiler.model.chat;

public class UserProfile {

    public int userId;
    public String firstName ="";
    public String lastName = "";
    public String userName = "";
    public String password = "";
    public String contactNo = "";
    public String contactEmail = "";
    public String lastLoginDate = "";
    public boolean userBlocked = false;
    public String bolckedDate = "";
    public boolean isFirstLogin =false;
    public String passwordExpiryDate;
    public int loginAttemptCount;
    public boolean isActive = false;
    public String userPhoto = "";
    public int createdBy;
    public String createdDate = "";
    public int updatedBy;
    public String updatedDate = "";
    public boolean isSuperAdmin = false;
    public boolean isPasswordReset = false;
}