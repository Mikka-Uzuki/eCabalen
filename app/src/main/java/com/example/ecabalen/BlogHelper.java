package com.example.ecabalen;

public class BlogHelper
{
    String BlogID, Email, FullName, Caption, Picture, DatePosted;

    public String getBlogID() {
        return BlogID;
    }

    public String getEmail() {
        return Email;
    }

    public String getFullName() {
        return FullName;
    }

    public String getCaption() {
        return Caption;
    }

    public String getPicture() {
        return Picture;
    }

    public String getDatePosted() {
        return DatePosted;
    }

    public BlogHelper(String blogID, String email, String fullName, String caption, String picture, String datePosted) {
        BlogID = blogID;
        Email = email;
        FullName = fullName;
        Caption = caption;
        Picture = picture;
        DatePosted = datePosted;
    }
}
