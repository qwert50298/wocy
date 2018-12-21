package com.unicom.autoship.bean;

import java.util.List;

public class ShipRouteResp extends BaseResp{

    private RouteData data;

    public ShipRouteResp(RouteData data) {
        this.data = data;
    }

    public RouteData getData() {
        return data;
    }

    public void setData(RouteData data) {
        this.data = data;
    }



    public class RouteData{

        private List<WayBean> way;

        public RouteData(List<WayBean> way) {
            this.way = way;
        }

        public List<WayBean> getWay() {
            return way;
        }

        public void setWay(List<WayBean> way) {
            this.way = way;
        }

        @Override
        public String toString() {
            return "RouteData{" +
                    "way=" + way +
                    '}';
        }
    }
}
