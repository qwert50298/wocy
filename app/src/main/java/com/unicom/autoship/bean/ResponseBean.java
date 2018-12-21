package com.unicom.autoship.bean;

public class ResponseBean<T> {


    private String code;
    private String message;
    private YsData data;
    private boolean success;

    public ResponseBean(String code, String message, YsData data, boolean success) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.success = success;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public YsData getData() {
        return data;
    }

    public void setData(YsData data) {
        this.data = data;
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

    public class YsData{
        private String accessToken;
        private long accessTokenExpire;
        private String accessTokenKey;

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

        public String getAccessTokenKey() {
            return accessTokenKey;
        }

        public void setAccessTokenKey(String accessTokenKey) {
            this.accessTokenKey = accessTokenKey;
        }

        @Override
        public String toString() {
            return "YsData{" +
                    "accessToken='" + accessToken + '\'' +
                    ", accessTokenExpire=" + accessTokenExpire +
                    ", accessTokenKey='" + accessTokenKey + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ResponseBean{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", success=" + success +
                '}';
    }
}
