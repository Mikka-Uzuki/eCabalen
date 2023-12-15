package com.example.ecabalen;

public class LocationImageHelper
{
    String LocationImageID, LocationID, Picture, DatePosted;

    public String getLocationImageID() {
        return LocationImageID;
    }

    public String getLocationID() {
        return LocationID;
    }

    public String getPicture() {
        return Picture;
    }

    public String getDatePosted() {
        return DatePosted;
    }

    public LocationImageHelper(String locationImageID, String locationID, String picture, String datePosted) {
        LocationImageID = locationImageID;
        LocationID = locationID;
        Picture = picture;
        DatePosted = datePosted;
    }
}
