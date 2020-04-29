package com.techsalt.tadmin.model;

import java.sql.Time;

public class TrackingHistoryModel {


    Time timeStamp;
    double lati,longi;


    public TrackingHistoryModel(Time timeStamp, double lati, double longi) {
        this.timeStamp = timeStamp;
        this.lati = lati;
        this.longi = longi;
    }

    public Time getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Time timeStamp) {
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
