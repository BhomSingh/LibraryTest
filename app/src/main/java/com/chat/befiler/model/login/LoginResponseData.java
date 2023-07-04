package com.chat.befiler.model.login;

import java.util.ArrayList;

public class LoginResponseData {

    public String fullName ="";
    public String email = "";
    public int userId =0;
    public String userName="";
    public String profileImageUrl = "";
    public String message = "";
    public String passwordExpiryDate;
    public String isFirstTimeLogin;
    public String token;
    public String validTo;
    public ArrayList<MenuAccessData> menuAccess;
    public AgentPermission agentRolePermissions;
    public boolean isSuperAdmin;
    public boolean isPasswordReset;

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getMessage() {
        return message;
    }

    public String getPasswordExpiryDate() {
        return passwordExpiryDate;
    }

    public String getIsFirstTimeLogin() {
        return isFirstTimeLogin;
    }

    public String getToken() {
        return token;
    }

    public String getValidTo() {
        return validTo;
    }

    public ArrayList<MenuAccessData> getMenuAccess() {
        return menuAccess;
    }

    public AgentPermission getAgentRolePermissions() {
        return agentRolePermissions;
    }

    public boolean isSuperAdmin() {
        return isSuperAdmin;
    }

    public boolean isPasswordReset() {
        return isPasswordReset;
    }
}