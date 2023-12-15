package com.example.ecabalen;

public class PhotoHelper
{
    String PhotoID, PlaceName, Picture, DatePosted;

    public String getPhotoID() {
        return PhotoID;
    }

    public String getPlaceName() {
        return PlaceName;
    }

    public String getPicture() {
        return Picture;
    }

    public String getDatePosted() {
        return DatePosted;
    }

    public PhotoHelper(String photoID, String placeName, String picture, String datePosted) {
        PhotoID = photoID;
        PlaceName = placeName;
        Picture = picture;
        DatePosted = datePosted;
    }
}