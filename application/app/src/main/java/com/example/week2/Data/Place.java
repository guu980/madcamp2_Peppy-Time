package com.example.week2.Data;

public class Place {
    private double longitude;
    private double latitude;
    private String title;

//    public Place(double longitude, double latitude, String title) {
//        this.longitude = longitude;
//        this.latitude = latitude;
//        this.title = title;
//    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
