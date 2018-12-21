package com.usr.usrcloudmqttsdkdemo.entity;

/**
 * Created by dyzhou on 2018/8/8.
 */

public class ReceiveDevStateMsg {

    private DevState devStatus;

    public DevState getDevStatus() {
        return devStatus;
    }

    public void setDevStatus(DevState devStatus) {
        this.devStatus = devStatus;
    }

    @Override
    public String toString() {
        return "ReceiveDevStateMsg{" +
                "devStatus=" + devStatus +
                '}';
    }
}
