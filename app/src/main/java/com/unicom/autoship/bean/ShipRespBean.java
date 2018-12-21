package com.unicom.autoship.bean;

import java.util.ArrayList;

public class ShipRespBean {

    private int code;
    private String message;
    private boolean success;
    public ShipRespData data;

    public ShipRespBean(int code, String message, boolean success, ShipRespData data) {
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

    public ShipRespData getData() {
        return data;
    }

    public void setData(ShipRespData data) {
        this.data = data;
    }

    /*class ShipRespData{
        ArrayList<ShipBean> shipList;

        public ArrayList<ShipBean> getShipList() {
            return shipList;
        }

        public void setShipList(ArrayList<ShipBean> shipList) {
            this.shipList = shipList;
        }
    }*/
    /*class ShipBean{
        private String boatDeviceId;
        private int boatDraft;
        private int boatEndurance;
        private int boatId;
        private int boatLength;
        private String boatMode;
        private String boatName;
        private String boatNote;
        private int boatSpeedAuto;
        private int boatSpeedMax;
        private String boatType;
        private int boatWeight;
        private BoatDeviceInfo boatDeviceInfo;

        public String getBoatDeviceId() {
            return boatDeviceId;
        }

        public void setBoatDeviceId(String boatDeviceId) {
            this.boatDeviceId = boatDeviceId;
        }

        public int getBoatDraft() {
            return boatDraft;
        }

        public void setBoatDraft(int boatDraft) {
            this.boatDraft = boatDraft;
        }

        public int getBoatEndurance() {
            return boatEndurance;
        }

        public void setBoatEndurance(int boatEndurance) {
            this.boatEndurance = boatEndurance;
        }

        public int getBoatId() {
            return boatId;
        }

        public void setBoatId(int boatId) {
            this.boatId = boatId;
        }

        public int getBoatLength() {
            return boatLength;
        }

        public void setBoatLength(int boatLength) {
            this.boatLength = boatLength;
        }

        public String getBoatMode() {
            return boatMode;
        }

        public void setBoatMode(String boatMode) {
            this.boatMode = boatMode;
        }

        public String getBoatName() {
            return boatName;
        }

        public void setBoatName(String boatName) {
            this.boatName = boatName;
        }

        public String getBoatNote() {
            return boatNote;
        }

        public void setBoatNote(String boatNote) {
            this.boatNote = boatNote;
        }

        public int getBoatSpeedAuto() {
            return boatSpeedAuto;
        }

        public void setBoatSpeedAuto(int boatSpeedAuto) {
            this.boatSpeedAuto = boatSpeedAuto;
        }

        public int getBoatSpeedMax() {
            return boatSpeedMax;
        }

        public void setBoatSpeedMax(int boatSpeedMax) {
            this.boatSpeedMax = boatSpeedMax;
        }

        public String getBoatType() {
            return boatType;
        }

        public void setBoatType(String boatType) {
            this.boatType = boatType;
        }

        public int getBoatWeight() {
            return boatWeight;
        }

        public void setBoatWeight(int boatWeight) {
            this.boatWeight = boatWeight;
        }

        public BoatDeviceInfo getBoatDeviceInfo() {
            return boatDeviceInfo;
        }

        public void setBoatDeviceInfo(BoatDeviceInfo boatDeviceInfo) {
            this.boatDeviceInfo = boatDeviceInfo;
        }
    }*/
}
