package com.unicom.autoship.bean;

public class BaseResp {

    private int code;
    private String message;
    private boolean success;


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

    @Override
    public String toString() {
        return "BaseResp{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", success=" + success +
                '}';
    }
}
