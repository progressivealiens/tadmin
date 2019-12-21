package com.techsalt.tadmin.model;

public class LocationModelOperations {

    private double la,lo;
    private String i,n,d;

    public LocationModelOperations(double la, double lo, String i, String n, String d) {
        this.la = la;
        this.lo = lo;
        this.i = i;
        this.n = n;
        this.d = d;
    }

    public double getLa() {
        return la;
    }

    public void setLa(double la) {
        this.la = la;
    }

    public double getLo() {
        return lo;
    }

    public void setLo(double lo) {
        this.lo = lo;
    }

    public String getI() {
        return i;
    }

    public void setI(String i) {
        this.i = i;
    }

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    public String getD() {
        return d;
    }

    public void setD(String d) {
        this.d = d;
    }
}
