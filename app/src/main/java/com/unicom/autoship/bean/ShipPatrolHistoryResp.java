package com.unicom.autoship.bean;

import java.util.List;

public class ShipPatrolHistoryResp extends BaseResp{

    private ShipPatrolData data;

    public ShipPatrolHistoryResp(ShipPatrolData data) {
        super();
        this.data = data;
    }

    public ShipPatrolData getData() {
        return data;
    }

    public void setData(ShipPatrolData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ShipPatrolHistoryResp{" +
                "data=" + data +
                '}';
    }

    public class ShipPatrolData{
        List<PatrolHistory> patrolHistories;

        public ShipPatrolData(List<PatrolHistory> patrolHistories) {
            this.patrolHistories = patrolHistories;
        }

        public List<PatrolHistory> getPatrolHistories() {
            return patrolHistories;
        }

        public void setPatrolHistories(List<PatrolHistory> patrolHistories) {
            this.patrolHistories = patrolHistories;
        }
    }
}
