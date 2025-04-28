package com.example.javaassignm;

import android.net.Uri;

public class FormItem {
    private String name;
    private String fname;
    private String date;
    private Uri imageUri;


    public FormItem(String name, String fname, String date, Uri imageUri) {
        this.name = name;
        this.fname = fname;
        this.date = date;
        this.imageUri = imageUri;
    }


    public String getName() {
        return name;
    }
    public String getFname(){
        return fname;
    }

    public String getDate() {
        return date;
    }

    public Uri getImageUri() {
        return imageUri;
    }
}
