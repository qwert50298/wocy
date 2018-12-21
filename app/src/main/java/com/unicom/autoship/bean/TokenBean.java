package com.unicom.autoship.bean;

public class TokenBean {

    private String accessToken;
    private long accessTokenExpire;
    private String accessApp;
    private String accessTokenKey;

    public TokenBean(String accessToken, long accessTokenExpire, String accessApp, String accessTokenKey) {
        this.accessToken = accessToken;
        this.accessTokenExpire = accessTokenExpire;
        this.accessApp = accessApp;
        this.accessTokenKey = accessTokenKey;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public long getAccessTokenExpire() {
        return accessTokenExpire;
    }

    public void setAccessTokenExpire(long accessTokenExpire) {
        this.accessTokenExpire = accessTokenExpire;
    }

    public String getAccessApp() {
        return accessApp;
    }

    public void setAccessApp(String accessApp) {
        this.accessApp = accessApp;
    }

    public String getAccessTokenKey() {
        return accessTokenKey;
    }

    public void setAccessTokenKey(String accessTokenKey) {
        this.accessTokenKey = accessTokenKey;
    }
}
