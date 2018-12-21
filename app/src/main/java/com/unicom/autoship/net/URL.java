package com.unicom.autoship.net;

import android.support.annotation.Nullable;
import android.util.Log;

import com.unicom.autoship.utils.HttpParameters;
import com.unicom.autoship.utils.Utils;
/**
 * Created by dyzhou on 2018/8/3.
 */
public class URL {

    private static final boolean isHttps = false;
    public static final String http = isHttps ? "https://" : "http://";
    public static final String host=http+"river.dev.wochanye.com";
    public static final String HTTP_POST = "POST";
    public static final String HTTP_GET = "GET";
    private static final int WebSocketPort =8088;// 11542;

    public static final String LOGIN_URL=host+"/apibase/login";
    public static final String REFRESH_YS_TOKEN=host+"/apibase/getAccessToken";
    public static final String GET_SHIP_LIST=host+"/apibase/boatInfoList";
    public static final String GET_SHIP_VIDEO_RECORD=host+"/apibase/basePatrolHistoryList";
    public static final String GET_SHIP_ROUTE="http://river.dev.wochanye.com/apiBoatData/boat/hisWay";
    public static final String POST_CONTROL=host+"/apibase/boatCtrlSubmit";
    public static final String GET_BOATINFO=host+"/apibase/boatInfo";


    // 用枚举重构请
    public enum SERVER {

        // 应用数据推送接口
        WEB_SOCKET(WebSocketPort, "river-controller.dev.wochanye.com/apiBoatData/", CMD.WEB_SOCKET, HTTP_POST);//"song90.imwork.net"


        public int port;
        public String servlet;
        public int cmd;
        public String method;

        SERVER (int port, String servlet, int cmd, String method) {
            this.port = port;
            this.servlet = servlet;
            this.cmd = cmd;
            this.method = method;
        }

        public String getUri (@Nullable HttpParameters pathParameters) {

            String BaseUrl ="http://river.dev.wochanye.com:8088";// "http://" +servlet;//+":"+port;

            //String BaseUrl="http://song90.imwork.net:11542";

            Log.i("URL------------",""+BaseUrl);
            return Utils.enCodePathParameters(BaseUrl, pathParameters);
        }

        public int getCmd () {
            return this.cmd;
        }
    }

    /**
     * CMD
     * 用户区分具体是那个接口的响应
     */
    public static final class CMD {

        public static final int WEB_SOCKET = 13;

    }
}
