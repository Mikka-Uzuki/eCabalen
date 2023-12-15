package com.example.ecabalen;

public class LocationHelper
{
    String Type, LocationID, LocationCategoryID, Name, Address, DatePosted, Rating;

    public String getType() {
        return Type;
    }

    public String getLocationID() {
        return LocationID;
    }

    public String getLocationCategoryID() {
        return LocationCategoryID;
    }

    public String getName() {
        return Name;
    }

    public String getAddress() {
        return Address;
    }

    public String getDatePosted() {
        return DatePosted;
    }

    public String getRating() {
        return Rating;
    }

    public LocationHelper(String type, String locationID, String locationCategoryID, String name, String address, String datePosted, String rating) {
        Type = type;
        LocationID = locationID;
        LocationCategoryID = locationCategoryID;
        Name = name;
        Address = address;
        DatePosted = datePosted;
        Rating = rating;
    }
}
