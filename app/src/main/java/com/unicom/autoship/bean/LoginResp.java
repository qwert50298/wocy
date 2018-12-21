package com.unicom.autoship.bean;

import java.util.List;

public class LoginResp {

    private int code;
    private String message;
    private boolean success;
    private LoginData data;

    public LoginResp(int code, String message, boolean success, LoginData data) {
        this.code = code;
        this.message = message;
        this.success = success;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public LoginData getData() {
        return data;
    }

    public void setData(LoginData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "LoginResp{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", success=" + success +
                ", data=" + data +
                '}';
    }

    public class LoginData{

        private String token;
        private long tokenExpire;
        private List<TokenBean> tokens;

        public LoginData(String token, long tokenExpire, List<TokenBean> tokens) {
            this.token = token;
            this.tokenExpire = tokenExpire;
            this.tokens = tokens;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public List<TokenBean> getTokens() {
            return tokens;
        }

        public void setTokens(List<TokenBean> tokens) {
            this.tokens = tokens;
        }

        public long getTokenExpire() {
            return tokenExpire;
        }

        public void setTokenExpire(long tokenExpire) {
            this.tokenExpire = tokenExpire;
        }
    }

}
