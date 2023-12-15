package com.example.ecabalen;

public class LocationReviewHelper
{
    String LocationReviewID, FullName, UserPicture, ReviewPicture, Description, Rating, DatePosted;

    public String getLocationReviewID() {
        return LocationReviewID;
    }

    public String getFullName() {
        return FullName;
    }

    public String getUserPicture() {
        return UserPicture;
    }

    public String getReviewPicture() {
        return ReviewPicture;
    }

    public String getDescription() {
        return Description;
    }

    public String getRating() {
        return Rating;
    }

    public String getDatePosted() {
        return DatePosted;
    }

    public LocationReviewHelper(String locationReviewID, String fullName, String userPicture, String reviewPicture, String description, String rating, String datePosted) {
        LocationReviewID = locationReviewID;
        FullName = fullName;
        UserPicture = userPicture;
        ReviewPicture = reviewPicture;
        Description = description;
        Rating = rating;
        DatePosted = datePosted;
    }
}
