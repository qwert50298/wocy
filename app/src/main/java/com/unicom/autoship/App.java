package com.unicom.autoship;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.baidu.mapapi.SDKInitializer;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.crashreport.CrashReport;
import com.unicom.autoship.net.URL;
import com.unicom.autoship.ui.util.Constants;
import com.unicom.autoship.utils.GsonRequest;

import com.unicom.autoship.utils.SocketHelper;
import com.usr.usrcloudmqttsdkdemo.base.UsrCrashHandler;
import com.videogo.openapi.EZOpenSDK;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;

import static android.content.ContentValues.TAG;

/**
 * Created by dyzhou on 2018/7/25.
 */

public class App extends Application {

    public static App instantce=null;
    public static boolean mainActivityLaunched = false;
    public static boolean isOnline = false;
    private RequestQueue queue;
    public static String accessToken;                             // 萤石token
    public static long expireTime;
    public static String token;//APP TOKEN
    public static long tokenExpire;
    public static String refreshToken;//刷新萤石TOKEN
    public static final String APP_KEY="e39515d46e1743b8bbdacd7ad6afb58a";//萤石
    public static final String APP_SECRET="c8233e15c243151fdce5c9cd24328ec4";//萤石
    public static final String YS_TOKEN_URL="https://open.ys7.com/api/lapp/token/get";//萤石
    public static boolean isConnect=false;//touchuan
    public static String account="chinaunicom";
    public static String password="china12345678";

    private UsrCrashHandler usrCrashHandler;
    private static Toast toast;
    public  static String USERNAME="";
    private RefWatcher mRefWatcher;
    public static App getInstance(){

        if(null==instantce){
            instantce=new App();
        }

        return instantce;
    }

    public static EZOpenSDK getOpenSDK() {
        return EZOpenSDK.getInstance();
    }

    // WebSocket 消息推送
    private SocketHelper socketHelper;

    {
        try {
            socketHelper = SocketHelper.getHttpsSingleton(URL.SERVER.WEB_SOCKET.getUri(null));
        } catch (URISyntaxException e) {

            Log.i("TAG","Websocket-------"+e.getMessage());
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instantce = this;
        getStorageData();
        //queue = Volley.newRequestQueue(getApplicationContext());
        /**百度地图SDK初始化**/
        SDKInitializer.initialize(getApplicationContext());
        /*** 海康萤石视频SDK初始化 */
        EZOpenSDK.showSDKLog(true);///** * sdk日志开关，正式发布需要去掉 */
        EZOpenSDK.enableP2P(false);///** * 设置是否支持P2P取流,详见api */
        EZOpenSDK.initLib(this, APP_KEY);///** * APP_KEY请替换成自己申请的 */
        Context context = getApplicationContext();
        // 获取当前包名
        String packageName = context.getPackageName();
        // 获取当前进程名
        String processName = getProcessName(android.os.Process.myPid());
        // 设置是否为上报进程
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
        strategy.setUploadProcess(processName == null || processName.equals(packageName));
        /***Bugly SDK初始化**/
        //CrashReport.initCrashReport(getApplicationContext(), "150c67595f", false);
        Bugly.init(getApplicationContext(), "150c67595f", false);
        /****LeakCanary****/
        mRefWatcher = Constants.DEBUG ?  LeakCanary.install(this) : RefWatcher.DISABLED;

        Logger.addLogAdapter(new AndroidLogAdapter());
    }

    // 获取本地数据
    private void getStorageData() {
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(this);
        //if (null != spf.getString("accessToken", null)) {
        isOnline=spf.getBoolean("isOnline",false);
        accessToken = spf.getString("accessToken", null);
        expireTime = spf.getLong("expireTime", 0L);

        //}

    }

    // 清理缓存的数据
    public void clearStorageData() {

        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .clear()
                .commit();
    }

    /**
     * 获取socket 实例
     *
     * @return
     */
    public SocketHelper getSocketHelper() {

        return socketHelper;
    }

    public RequestQueue getRequestQueue() {
        if (queue == null) {
            queue = Volley.newRequestQueue(getApplicationContext());
        }
        return queue;
    }

    public void addRequest(GsonRequest request) {

        getRequestQueue().add(request);
    }

    public void addRequest(StringRequest request) {

        getRequestQueue().add(request);
    }


    /**
     * 初始化异常处理类
     */
    private void initUsrCrashHandler() {
        usrCrashHandler = UsrCrashHandler.getmusrCrashHandler();
        if (usrCrashHandler != null) {
            usrCrashHandler.initCrashHandler(this);
        }
    }

    @Override
    public void onTerminate() {
        //程序终止的时候执行
        Log.d(TAG, "EpcApplication-----------onTerminate程序终止的时候执行");
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        //低内存的时候执行
        Log.d(TAG, "EpcApplication-----------onLowMemory低内存的时候执行");
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        // 程序在内存清理的时候执行
        Log.d(TAG, "EpcApplication-----------onTrimMemory 程序在内存清理的时候执行");
        super.onTrimMemory(level);
    }

    public static void showToast(Context context, String msg) {
        if (toast == null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }

    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    private static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    public static RefWatcher getRefWatcher() {
        return getInstance().mRefWatcher;
    }

}
