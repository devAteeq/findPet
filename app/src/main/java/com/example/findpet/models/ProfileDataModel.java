package com.example.findpet.models;

public class ProfileDataModel {
    private String name;
    private String phone;
    private String email;
    private String uId;
    private String imageUrl;

    public ProfileDataModel() {
    }

    public ProfileDataModel(String name, String phone, String email, String uId, String imageUrl) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.uId = uId;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getuId() {
        return uId;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
