package com.techsalt.tadmin.model;

public class TrackingHistoryModel {


    String timeStamp;
    double lati,longi;


    public TrackingHistoryModel(String timeStamp, double lati, double longi) {
        this.timeStamp = timeStamp;
        this.lati = lati;
        this.longi = longi;
    }


    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public double getLati() {
        return lati;
    }

    public void setLati(double lati) {
        this.lati = lati;
    }

    public double getLongi() {
        return longi;
    }

    public void setLongi(double longi) {
        this.longi = longi;
    }
}
