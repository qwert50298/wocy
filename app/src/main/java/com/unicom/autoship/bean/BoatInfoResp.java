package com.unicom.autoship.bean;

import java.util.List;

public class BoatInfoResp extends BaseResp{

    private BoatInfoData data;

    public BoatInfoResp(BoatInfoData data) {
        this.data = data;
    }

    public BoatInfoData getData() {
        return data;
    }

    public void setData(BoatInfoData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "BoatInfoResp{" +
                "data=" + data +
                '}';
    }
}
