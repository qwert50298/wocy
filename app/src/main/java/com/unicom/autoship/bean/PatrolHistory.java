package com.unicom.autoship.bean;

import java.util.List;

public class PatrolHistory {

    private int id;
    private String picture;
    private String patrolNo;
    private String boatId;
    private String boatDeviceId;
    private String reach;
    private String reachIds;
    private String boatName;
    private long patrolStartTime;
    private long patrolEndTime;
    private String patrolStartTimeStr;
    private String patrolEndTimeStr;
    private String duration;
    private List<BoatDeviceInfo> boatDeviceInfo;

    public PatrolHistory(int id, String picture, String patrolNo, String boatId, String boatDeviceId, String reach, String reachIds, String boatName, long patrolStartTime, long patrolEndTime, String patrolStartTimeStr, String patrolEndTimeStr, String duration, List<BoatDeviceInfo> boatDeviceInfo) {
        this.id = id;
        this.picture = picture;
        this.patrolNo = patrolNo;
        this.boatId = boatId;
        this.boatDeviceId = boatDeviceId;
        this.reach = reach;
        this.reachIds=reachIds;
        this.boatName = boatName;
        this.patrolStartTime = patrolStartTime;
        this.patrolEndTime = patrolEndTime;
        this.patrolStartTimeStr = patrolStartTimeStr;
        this.patrolEndTimeStr = patrolEndTimeStr;
        this.duration = duration;
        this.boatDeviceInfo = boatDeviceInfo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getPatrolNo() {
        return patrolNo;
    }

    public void setPatrolNo(String patrolNo) {
        this.patrolNo = patrolNo;
    }

    public String getBoatId() {
        return boatId;
    }

    public void setBoatId(String boatId) {
        this.boatId = boatId;
    }

    public String getBoatDeviceId() {
        return boatDeviceId;
    }

    public void setBoatDeviceId(String boatDeviceId) {
        this.boatDeviceId = boatDeviceId;
    }

    public String getReach() {
        return reach;
    }

    public void setReach(String reach) {
        this.reach = reach;
    }

    public String getReachIds() {
        return reachIds;
    }

    public void setReachIds(String reachIds) {
        this.reachIds = reachIds;
    }

    public String getBoatName() {
        return boatName;
    }

    public void setBoatName(String boatName) {
        this.boatName = boatName;
    }

    public long getPatrolStartTime() {
        return patrolStartTime;
    }

    public void setPatrolStartTime(long patrolStartTime) {
        this.patrolStartTime = patrolStartTime;
    }

    public long getPatrolEndTime() {
        return patrolEndTime;
    }

    public void setPatrolEndTime(long patrolEndTime) {
        this.patrolEndTime = patrolEndTime;
    }

    public String getPatrolStartTimeStr() {
        return patrolStartTimeStr;
    }

    public void setPatrolStartTimeStr(String patrolStartTimeStr) {
        this.patrolStartTimeStr = patrolStartTimeStr;
    }

    public String getPatrolEndTimeStr() {
        return patrolEndTimeStr;
    }

    public void setPatrolEndTimeStr(String patrolEndTimeStr) {
        this.patrolEndTimeStr = patrolEndTimeStr;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public List<BoatDeviceInfo> getBoatDeviceInfo() {
        return boatDeviceInfo;
    }

    public void setBoatDeviceInfo(List<BoatDeviceInfo> boatDeviceInfo) {
        this.boatDeviceInfo = boatDeviceInfo;
    }
}
