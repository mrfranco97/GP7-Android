package com.example.xplorenow_android;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("email")
    private String email;
    @SerializedName("phone")
    private String phone;
    @SerializedName("role")
    private String role;
    @SerializedName("preferences")
    private UserPreferences preferences;

    public User(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public User(UserPreferences preferences) {
        this.preferences = preferences;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getRole() { return role; }
    public UserPreferences getPreferences() { return preferences; }
    public void setPreferences(UserPreferences preferences) { this.preferences = preferences; }
}
