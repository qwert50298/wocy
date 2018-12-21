package com.unicom.autoship.bean;

public class BoatDeviceInfo {
    private String deviceType;
    private String deviceChannel;
    private String deviceSerial;
    private String verifyCode;

    public BoatDeviceInfo(String deviceType, String deviceChannel, String deviceSerial, String verifyCode) {
        this.deviceType = deviceType;
        this.deviceChannel = deviceChannel;
        this.deviceSerial = deviceSerial;
        this.verifyCode = verifyCode;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceChannel() {
        return deviceChannel;
    }

    public void setDeviceChannel(String deviceChannel) {
        this.deviceChannel = deviceChannel;
    }

    public String getDeviceSerial() {
        return deviceSerial;
    }

    public void setDeviceSerial(String deviceSerial) {
        this.deviceSerial = deviceSerial;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }
}
