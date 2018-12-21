package com.unicom.autoship.common;

import java.io.Serializable;

public class MarkerInfoUtil implements Serializable {

    private static final long serialVersionUID = 8633299996744734593L;

    private double longitude;//经度
    private double latitude;//纬度
    private String device_name;
    private String device_id;
    private String video_id;
    private int video_no;
    private String video_code;

    //构造方法
    public MarkerInfoUtil() {}

    public MarkerInfoUtil(double longitude, double latitude, String device_name, String device_id, String video_id, int video_no, String video_code) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.device_name = device_name;
        this.device_id = device_id;
        this.video_id = video_id;
        this.video_no = video_no;
        this.video_code = video_code;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
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

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public int getVideo_no() {
        return video_no;
    }

    public void setVideo_no(int video_no) {
        this.video_no = video_no;
    }

    public String getVideo_code() {
        return video_code;
    }

    public void setVideo_code(String video_code) {
        this.video_code = video_code;
    }

    @Override
    public String toString() {
        return "MarkerInfoUtil{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                ", device_name='" + device_name + '\'' +
                ", device_id='" + device_id + '\'' +
                ", video_id='" + video_id + '\'' +
                ", video_no=" + video_no +
                ", video_code='" + video_code + '\'' +
                '}';
    }
}
