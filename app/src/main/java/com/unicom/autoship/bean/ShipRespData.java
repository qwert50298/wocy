package com.unicom.autoship.bean;

import java.util.ArrayList;

public class ShipRespData {

    private double longitude;
    private double latitude;
    private ArrayList<ShipBean> boats;

    public ShipRespData(double longitude, double latitude, ArrayList<ShipBean> boats) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.boats = boats;
    }

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

    public ArrayList<ShipBean> getBoats() {
        return boats;
    }

    public void setBoats(ArrayList<ShipBean> boats) {
        this.boats = boats;
    }
}
