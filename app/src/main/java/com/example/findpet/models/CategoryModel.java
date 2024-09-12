package com.example.findpet.models;

import java.io.Serializable;

public class CategoryModel implements Serializable {
    private String id;
    private String categoryImage;
    private String categoryName;

    public String getCategoryImage() {
        return categoryImage;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getId() {
        return id;
    }
}
