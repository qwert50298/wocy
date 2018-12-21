package com.usr.usrcloudmqttsdkdemo.entity;

/**
 * Created by dyzhou on 2018/8/8.
 */

public class DataPoints {
    private String pointId;
    private String value;

    public String getPointId() {
        return pointId;
    }

    public void setPointId(String pointId) {
        this.pointId = pointId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "DataPoints{" +
                "pointId='" + pointId + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
