package com.unicom.autoship.bean;

import java.util.List;

public class ShipBean {

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
    private List<BoatDeviceInfo> boatDeviceInfo;
    private double boatLongitude;
    private double boatLatitude;

    public ShipBean(String boatDeviceId, int boatDraft, int boatEndurance, int boatId, int boatLength, String boatMode, String boatName, String boatNote,
                    int boatSpeedAuto, int boatSpeedMax, String boatType, int boatWeight, List<BoatDeviceInfo> boatDeviceInfo, double boatLongitude, double boatLatitude) {
        this.boatDeviceId = boatDeviceId;
        this.boatDraft = boatDraft;
        this.boatEndurance = boatEndurance;
        this.boatId = boatId;
        this.boatLength = boatLength;
        this.boatMode = boatMode;
        this.boatName = boatName;
        this.boatNote = boatNote;
        this.boatSpeedAuto = boatSpeedAuto;
        this.boatSpeedMax = boatSpeedMax;
        this.boatType = boatType;
        this.boatWeight = boatWeight;
        this.boatDeviceInfo = boatDeviceInfo;
        this.boatLongitude = boatLongitude;
        this.boatLatitude = boatLatitude;
    }

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

    public List<BoatDeviceInfo> getBoatDeviceInfo() {
        return boatDeviceInfo;
    }

    public void setBoatDeviceInfo(List<BoatDeviceInfo> boatDeviceInfo) {
        this.boatDeviceInfo = boatDeviceInfo;
    }

    public double getBoatLongitude() {
        return boatLongitude;
    }

    public void setBoatLongitude(double boatLongitude) {
        this.boatLongitude = boatLongitude;
    }

    public double getBoatLatitude() {
        return boatLatitude;
    }

    public void setBoatLatitude(double boatLatitude) {
        this.boatLatitude = boatLatitude;
    }
}
