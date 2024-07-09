package com.example.restauranthub;

public class mapModel {
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public mapModel(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    double latitude;
    double longitude;
}
