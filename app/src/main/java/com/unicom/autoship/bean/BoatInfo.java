package com.unicom.autoship.bean;

public class BoatInfo {
    private String timeStampStr;
    private String boatNo;
    private String riverId;
    private double lon;
    private double lat;
    private int vel;

    public BoatInfo(String timeStampStr, String boatNo, String riverId, double lon, double lat, int vel) {
        this.timeStampStr = timeStampStr;
        this.boatNo = boatNo;
        this.riverId = riverId;
        this.lon = lon;
        this.lat = lat;
        this.vel = vel;
    }

    public String getTimeStampStr() {
        return timeStampStr;
    }

    public void setTimeStampStr(String timeStampStr) {
        this.timeStampStr = timeStampStr;
    }

    public String getBoatNo() {
        return boatNo;
    }

    public void setBoatNo(String boatNo) {
        this.boatNo = boatNo;
    }

    public String getRiverId() {
        return riverId;
    }

    public void setRiverId(String riverId) {
        this.riverId = riverId;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public int getVel() {
        return vel;
    }

    public void setVel(int vel) {
        this.vel = vel;
    }

    @Override
    public String toString() {
        return "BoatInfo{" +
                "timeStampStr='" + timeStampStr + '\'' +
                ", boatNo='" + boatNo + '\'' +
                ", riverId='" + riverId + '\'' +
                ", lon=" + lon +
                ", lat=" + lat +
                ", vel=" + vel +
                '}';
    }
}
