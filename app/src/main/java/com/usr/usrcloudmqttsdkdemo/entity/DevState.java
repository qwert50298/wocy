package com.usr.usrcloudmqttsdkdemo.entity;

/**
 * Created by dyzhou on 2018/8/8.
 */

public class DevState {

    private String devName;

    private int status;

    public String getDevName() {
        return devName;
    }

    public void setDevName(String devName) {
        this.devName = devName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "DevState{" +
                "devName='" + devName + '\'' +
                ", status=" + status +
                '}';
    }
}
