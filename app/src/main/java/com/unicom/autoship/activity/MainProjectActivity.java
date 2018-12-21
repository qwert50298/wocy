package com.unicom.autoship.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baidu.mapapi.map.MapView;
import com.unicom.autoship.App;
import com.unicom.autoship.MainActivity;
import com.unicom.autoship.R;
import com.unicom.autoship.bean.ResponseBean;
import com.unicom.autoship.fragment.BoatMapFragment;
import com.unicom.autoship.fragment.ContactPageFragment;
import com.unicom.autoship.fragment.HomePageFragment;
import com.unicom.autoship.fragment.MinePageFragment;
import com.unicom.autoship.net.URL;
import com.unicom.autoship.ui.BottomBar;
import com.unicom.autoship.utils.GsonRequest;
import com.unicom.autoship.utils.StatusBarUtil;
import com.unicom.autoship.utils.Utils;
import com.usr.usrcloudmqttsdkdemo.business.UsrCloudClientService;
import com.videogo.openapi.EZOpenSDK;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

import static com.unicom.autoship.App.accessToken;
import static com.unicom.autoship.App.expireTime;


public class MainProjectActivity extends BaseActivity implements BottomBar.BottomBarClickListener {

    private Receiver receiver;
    private static final String TAG = "MainProjectActivity";
    //private SupportFragment[] mFragments = new SupportFragment[4];
    Fragment[] mFragments = new Fragment[4];
    Fragment fragment = null;
    HomePageFragment homeFragment;
    BoatMapFragment boatFragment;
    ContactPageFragment contactFragment;
    MinePageFragment mineFragment;
    int tag;
    /*@BindView(R.id.container)
    FrameLayout container;
    @BindView(R.id.bottomBar)*/
    BottomBar bottomBar;

    @Override
    public int rootViewRes() {
        return R.layout.activity_main_project;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main_project);

        StatusBarUtil.setTranslucentForImageViewInFragment(this, 0, null);
        App.mainActivityLaunched = true;

        receiver = new Receiver();
        //新添代码，在代码中注册广播接收程序
        IntentFilter filter = new IntentFilter();
        filter.addAction("onConnectAck");
        registerReceiver(receiver, filter);

        Bundle bundle = new Bundle();
        bundle.putString("uname", App.account);//"SkypeLink");//
        bundle.putString("upw", App.password);//"yao1991");//
        App.USERNAME=App.account;
        startServiceWithParm(UsrCloudClientService.class, bundle);
        bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnBottomBarClickListener(this);

        if (null == savedInstanceState) {
            onBottomBarClicked(BottomBar.HOME_ITEM);
            bottomBar.findViewById(R.id.tv_home).setSelected(true);
        }


        String accessToken = getDefaultPref().getString("accessToken","");

        boolean isNetworkEnable = Utils.isNetworkEnable(MainProjectActivity.this);
        if(!"".equals(accessToken)&&null!=accessToken) {
            long expireTime=getDefaultPref().getLong("expireTime",0L);
            Long time = System.currentTimeMillis();
            Log.i("TAG","TIME-----"+time+"---------"+App.expireTime);
            if (time < expireTime) {
                Log.i("TAG", "自动登录---Main");
                EZOpenSDK.getInstance().setAccessToken(accessToken);

            } else {

                if (isNetworkEnable) {
                    Log.i("TAG", "检验---获取token");
                    getToken();

                } else {
                    Toast.makeText(MainProjectActivity.this, "当前网络不可用，请检查网络配置！", Toast.LENGTH_SHORT);
                }
            }
        }else{
            if (isNetworkEnable) {
                Log.i("TAG", "检验---获取token");
                getToken();
            } else {
                Toast.makeText(MainProjectActivity.this, "当前网络不可用，请检查网络配置！", Toast.LENGTH_SHORT);
            }
        }
    }


    public class Receiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            if (intent.getIntExtra("onConnectAckreturnCode", 1) == 0) {
                App.isConnect=true;
                Log.i("TAG","TOUCHUAN connect success!");
                //startActivity(new Intent(MainActivity.this, MainDisplayActivity.class));
            } else if (intent.getIntExtra("onConnectAckreturnCode", 1) == 1) {
                App.isConnect=false;
                Toast.makeText(MainProjectActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
            }

        }
    }


    public void onBottomBarClicked(int item) {

        String tag;

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (homeFragment != null) {
            transaction.hide(homeFragment);
        }
        if (boatFragment != null) {
            transaction.hide(boatFragment);
        }
        if (contactFragment != null) {
            transaction.hide(contactFragment);
        }
        if (mineFragment != null) {
            transaction.hide(mineFragment);
        }

        switch (item) {
            case BottomBar.HOME_ITEM:
                tag = HomePageFragment.TAG;
                if (null == homeFragment) {
                    homeFragment = HomePageFragment.newInstance();
                    transaction.add(R.id.container, homeFragment, tag);
                }
                fragment = homeFragment;
                break;
            case BottomBar.BOAT_ITEM:
                tag = BoatMapFragment.TAG;
                if (null == boatFragment) {
                    boatFragment = BoatMapFragment.newInstance();
                    transaction.add(R.id.container, boatFragment, tag);
                }
                fragment = boatFragment;
                break;
            case BottomBar.CONTACT_ITEM:
                tag = ContactPageFragment.TAG;
                if (null == contactFragment) {
                    contactFragment = ContactPageFragment.newInstance();
                    transaction.add(R.id.container, contactFragment, tag);
                }
                fragment = contactFragment;
                break;
            case BottomBar.MINE_ITEM:
                tag = MinePageFragment.TAG;
                if (null == mineFragment) {
                    mineFragment = MinePageFragment.newInstance();
                    transaction.add(R.id.container, mineFragment, tag);
                }
                fragment = mineFragment;
                break;
        }
        if (null != fragment) {

            transaction.show(fragment)
                    .commit();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null!=receiver)
            this.unregisterReceiver(receiver);

    }

    public void getToken() {
        Map<String, String> params = new HashMap<>();
        params.put("tokenKey",App.refreshToken);
        /*params.put("appKey", App.APP_KEY);
        params.put("appSecret", App.APP_SECRET);*/
        String url = URL.REFRESH_YS_TOKEN;//App.YS_TOKEN_URL;
        GsonRequest<ResponseBean> request = new GsonRequest<ResponseBean>(
                url,
                ResponseBean.class,
                params,
                new Response.Listener<ResponseBean>() {
                    @Override
                    public void onResponse(ResponseBean response) {
                        Log.i("TAG","YS----Response-----"+response.toString());

                        if(response.isSuccess()){

                            ResponseBean.YsData data = response.getData();
                            App.accessToken=data.getAccessToken();
                            App.expireTime=data.getAccessTokenExpire();
                            App.refreshToken=data.getAccessTokenKey();
                            Log.i("TAG","getYS-----accessToken-----"+accessToken+"----expireTime---"+expireTime);
                            EZOpenSDK.getInstance().setAccessToken(App.accessToken);
                            //SharedPreferences.Editor editor= sp.edit();
                            SharedPreferences.Editor editor = getDefaultPref().edit();
                            editor.putString("accessToken", App.accessToken);
                            editor.putLong("expireTime", App.expireTime);
                            boolean isOK=editor.commit();
                            Log.i("TAG","RESULT-----"+isOK);


                        }else{
                            Toast.makeText(MainProjectActivity.this,response.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                    }
                }
        );
        App.getInstance().addRequest(request);


    }
}
