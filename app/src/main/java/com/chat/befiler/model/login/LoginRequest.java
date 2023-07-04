package com.chat.befiler.model.login;

public class LoginRequest {
    private  String email;
    private  String password;
    private  String deviceToken;

    public LoginRequest(String email, String password, String deviceToken) {
        this.email = email;
        this.password = password;
        this.deviceToken = deviceToken;
    }
}
