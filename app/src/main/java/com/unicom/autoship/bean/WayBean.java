package com.unicom.autoship.bean;

public class WayBean {

    private String id;
    private String boatNo;
    private double lon;
    private double lat;
    private String timeStampStr;
    private long timeStampLong;
    private int vel;
    private int alt;
    private String content;
    private String place;
    private String mediaUrl;

    public WayBean(String id, String boatNo, double lon, double lat, String timeStampStr, long timeStampLong, int vel, int alt, String content, String place, String mediaUrl) {
        this.id = id;
        this.boatNo = boatNo;
        this.lon = lon;
        this.lat = lat;
        this.timeStampStr = timeStampStr;
        this.timeStampLong = timeStampLong;
        this.vel = vel;
        this.alt = alt;
        this.content = content;
        this.place = place;
        this.mediaUrl = mediaUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBoatNo() {
        return boatNo;
    }

    public void setBoatNo(String boatNo) {
        this.boatNo = boatNo;
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

    public String getTimeStampStr() {
        return timeStampStr;
    }

    public void setTimeStampStr(String timeStampStr) {
        this.timeStampStr = timeStampStr;
    }

    public long getTimeStampLong() {
        return timeStampLong;
    }

    public void setTimeStampLong(long timeStampLong) {
        this.timeStampLong = timeStampLong;
    }

    public int getVel() {
        return vel;
    }

    public void setVel(int vel) {
        this.vel = vel;
    }

    public int getAlt() {
        return alt;
    }

    public void setAlt(int alt) {
        this.alt = alt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    @Override
    public String toString() {
        return "WayBean{" +
                "id='" + id + '\'' +
                ", boatNo='" + boatNo + '\'' +
                ", lon=" + lon +
                ", lat=" + lat +
                ", timeStampStr='" + timeStampStr + '\'' +
                ", timeStampLong=" + timeStampLong +
                ", vel=" + vel +
                ", alt=" + alt +
                ", content='" + content + '\'' +
                ", place='" + place + '\'' +
                ", mediaUrl='" + mediaUrl + '\'' +
                '}';
    }
}
