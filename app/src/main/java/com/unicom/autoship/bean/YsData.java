package com.unicom.autoship.bean;

public class YsData {

    private String accessToken;
    private long expireTime;

    public YsData(String accessToken, long expireTime) {
        this.accessToken = accessToken;
        this.expireTime = expireTime;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    @Override
    public String toString() {
        return "YsData{" +
                "accessToken='" + accessToken + '\'' +
                ", expireTime=" + expireTime +
                '}';
    }
}
