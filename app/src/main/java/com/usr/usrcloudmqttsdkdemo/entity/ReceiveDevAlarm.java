package com.usr.usrcloudmqttsdkdemo.entity;

/**
 * Created by dyzhou on 2018/8/8.
 */

public class ReceiveDevAlarm {

    private DevAlarm devAlarm;

    public DevAlarm getDevAlarm() {
        return devAlarm;
    }

    public void setDevAlarm(DevAlarm devAlarm) {
        this.devAlarm = devAlarm;
    }

    @Override
    public String toString() {
        return "ReceiveDevAlarm{" +
                "devAlarm=" + devAlarm +
                '}';
    }
}
