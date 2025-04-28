package com.example.javaassignm;

import android.net.Uri;

public class FormItem {
    private String name;
    private String date;
    private Uri imageUri;


    public FormItem(String name, String date, Uri imageUri) {
        this.name = name;
        this.date = date;
        this.imageUri = imageUri;
    }


    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public Uri getImageUri() {
        return imageUri;
    }
}
