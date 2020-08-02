package com.connectapp.user.chatmodel;

import java.io.Serializable;

public class UserChatClass implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String name = "";
    private String userId = "";
    private String databaseId = "";
    private String phone = "";
    private String email = "";
    private String status = "";
    public String displayName = "";
    public String profileUrl = "";
    public String firebaseId = "";
    public String adminFirebaseId = "";
    public String firebaseInstanceId = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(String databaseId) {
        this.databaseId = databaseId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
