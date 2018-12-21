package com.unicom.autoship.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.animation.Animation;
import com.baidu.mapapi.animation.ScaleAnimation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Text;
import com.baidu.mapapi.model.LatLng;
import com.unicom.autoship.App;
import com.unicom.autoship.MainActivity;
import com.unicom.autoship.R;
import com.unicom.autoship.bean.BoatDeviceInfo;
import com.unicom.autoship.bean.ResponseBean;
import com.unicom.autoship.bean.ShipBean;
import com.unicom.autoship.bean.ShipRespBean;
import com.unicom.autoship.bean.ShipRespData;
import com.unicom.autoship.common.MarkerInfoUtil;
import com.unicom.autoship.net.URL;
import com.unicom.autoship.ui.dialoglikeios.DialogClickListener;
import com.unicom.autoship.utils.GsonRequest;
import com.unicom.autoship.utils.Utils;
import com.usr.usrcloudmqttsdkdemo.business.UsrCloudClientService;
import com.videogo.openapi.EZOpenSDK;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.unicom.autoship.App.accessToken;
import static com.unicom.autoship.App.expireTime;

public class MainDrawerActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Receiver receiver;
    MapView mMapView;
    private BaiduMap mBaiduMap;
    //模式切换，正常模式
    private boolean modeFlag = true;
    //当前地图缩放级别
    private float zoomLevel;
    //定位相关
    private LocationClient mLocationClient;
    private MyLocationListener mLocationListener;
    //是否第一次定位，如果是第一次定位的话要将自己的位置显示在地图 中间
    private boolean isFirstLocation = true;
    //创建自己的箭头定位
    private BitmapDescriptor bitmapDescriptor;
    //经纬度
    double mLatitude;
    double mLongitude;
    //预设经纬度
    double myLatitude;
    double myLongitude;
    private ArrayList<ShipBean> alist=new ArrayList<>();
    private ArrayList<MarkerInfoUtil> markers=new ArrayList<>();
    private int height;
    private int width;
    private boolean isError=true;
    private boolean isInit=true;
    //private SwipeRefreshLayout swipeRefreshLayout;
    public static int dis=0;
    private String showStr="";

    @Override
    public int rootViewRes() {
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setMapCustomFile(this,"custom_config_0321.json");//0321清新蓝、0322黑色、0323午夜蓝
        setContentView(R.layout.activity_main_drawer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //toolbar.setTitle("智慧巡河");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, ""+showStr, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TextView tv_version = navigationView.getHeaderView(0).findViewById(R.id.textView);
        tv_version.setText("版本:"+Utils.getAppVersionName("v"));


        DisplayMetrics dm = getResources().getDisplayMetrics();
        height = dm.heightPixels;
        width = dm.widthPixels;
        Log.i("TAG","Width:----"+width+"-----Height:---"+height);
        MapView.setMapCustomEnable(true);


        App.mainActivityLaunched = true;
        initView();


        boolean isNetworkEnable = Utils.isNetworkEnable(this);
        if (isNetworkEnable) {
            showProgressDialog("加载中...");
            getShipList();
        }else {
            Toast.makeText(MainDrawerActivity.this,"当前网络不可用，请检查网络配置！",Toast.LENGTH_SHORT);
        }

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
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        /*showIosChoiceDialog("确认退出应用？","取消","确定",new DialogClickListener(){
            @Override
            public void onClick(View v) {
                super.onClick(v);
                App.isOnline = false;
                SharedPreferences.Editor editor = getDefaultPref().edit();
                editor.putString("accessToken", "");
                editor.putLong("expireTime", 0);
                editor.putString("token", "");
                editor.putString("refreshToken", "");
                editor.putString("account", "");
                editor.putString("pswd", "");
                editor.putBoolean("isOnline", false);
                editor.commit();
                //EZOpenSDK.getInstance().logout();
                Intent i = new Intent(MainDrawerActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }});*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            Intent i = new Intent(MainDrawerActivity.this, ShipVideoRecordListActivity.class);
            startActivity(i);
        }/* else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        }*/ else if (id == R.id.nav_send) {
            //App.getInstance().clearStorageData();
            showIosChoiceDialog("确认退出应用？","取消","确定",new DialogClickListener(){
                @Override
                public void onClick(View v) {
                    super.onClick(v);
                    App.isOnline=false;
                    SharedPreferences.Editor editor = getDefaultPref().edit();
                    editor.putString("accessToken", "");
                    editor.putLong("expireTime", 0);
                    editor.putString("token","");
                    editor.putString("refreshToken","");
                    editor.putString("account","");
                    editor.putString("pswd","");
                    editor.putBoolean("isOnline", false);
                    editor.commit();
                    //EZOpenSDK.getInstance().logout();
                    Intent i = new Intent(MainDrawerActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void initView(){
        /*((Button)findViewById(R.id.btn_quit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //App.getInstance().clearStorageData();
                showIosChoiceDialog("确认退出应用？","取消","确定",new DialogClickListener(){
                    @Override
                    public void onClick(View v) {
                        super.onClick(v);
                        App.isOnline=false;
                        SharedPreferences.Editor editor = getDefaultPref().edit();
                        editor.putString("accessToken", "");
                        editor.putLong("expireTime", 0);
                        editor.putString("token","");
                        editor.putString("refreshToken","");
                        editor.putString("account","");
                        editor.putString("pswd","");
                        editor.putBoolean("isOnline", false);
                        editor.commit();
                        //EZOpenSDK.getInstance().logout();
                        Intent i = new Intent(MainDrawerActivity.this, LoginActivity.class);
                        startActivity(i);
                        finish();
                    }
                });

            }
        });

        ((Button)findViewById(R.id.btn_record)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainDrawerActivity.this, ShipVideoRecordListActivity.class);
                startActivity(i);
            }
        });*/

        initMap();


        String accessToken = getDefaultPref().getString("accessToken","");



        long expireTime=getDefaultPref().getLong("expireTime",0L);
        Long time = System.currentTimeMillis();
        Log.i("TAG","TIME-----"+time+"---------"+App.expireTime);
        if (time <expireTime) {
            Log.i("TAG","自动登录---Main");
            EZOpenSDK.getInstance().setAccessToken(accessToken);

        } else{
            boolean isNetworkEnable = Utils.isNetworkEnable(MainDrawerActivity.this);
            if (isNetworkEnable) {
                Log.i("TAG", "检验---获取token");
                getToken();

            }else {
                Toast.makeText(MainDrawerActivity.this,"当前网络不可用，请检查网络配置！",Toast.LENGTH_SHORT);
            }

        }

    }

    public void getShipList() {

        if(null==App.token)  App.token= getDefaultPref().getString("token","");

        Map<String, String> params = new HashMap<>();
        params.put("token",App.token);
        String url = URL.GET_SHIP_LIST;
        GsonRequest<ShipRespBean> request = new GsonRequest<ShipRespBean>(
                url,
                ShipRespBean.class,
                new Response.Listener<ShipRespBean>() {
                    @Override
                    public void onResponse(ShipRespBean response) {
                        Log.i("TAG","ShipList----Response-----"+response.toString());

                        hideProgressDialog();
                        //if(!isInit){
                        //swipeRefreshLayout.setRefreshing(false);
                        //}
                        if(response.isSuccess()){
                            isError=false;
                            ShipRespData data  = response.getData();
                            myLongitude=data.getLongitude();
                            myLatitude=data.getLatitude();
                            alist= data.getBoats();

                            LatLng ll = new LatLng(myLatitude, myLongitude);
                            MapStatusUpdate status = MapStatusUpdateFactory.newLatLng(ll);
                            //mBaiduMap.setMapStatus(status);//直接到中间
                            mBaiduMap.animateMapStatus(status);//动画的方式到中间


                            if(0==alist.size()){
                                showStr="当前无船巡查";
                                ((TextView) findViewById(R.id.tv_tips)).setText("当前无船巡查");
                            }else{
                                showStr="当前有"+alist.size() +"条船正在巡查";
                                ((TextView) findViewById(R.id.tv_tips)).setText("当前有"+alist.size() +"条船正在巡查");
                                markers = new ArrayList<>();

                                for (ShipBean bean : alist) {

                                    List<BoatDeviceInfo> deviceInfos = bean.getBoatDeviceInfo();
                                    String deviceSerial = "";
                                    String cameraNo = "";
                                    String verifyCode = "";
                                    if (null != deviceInfos) {
                                        for (BoatDeviceInfo device : deviceInfos) {
                                            if (null != device.getDeviceType() && "camera".equals(device.getDeviceType())) {
                                                deviceSerial = device.getDeviceSerial();
                                                cameraNo = device.getDeviceChannel();
                                                verifyCode = device.getVerifyCode();
                                            }
                                        }
                                    }
                                    MarkerInfoUtil markerInfo = new MarkerInfoUtil(bean.getBoatLongitude(), bean.getBoatLatitude(), "" + bean.getBoatName(), "" + bean.getBoatDeviceId(), "" + deviceSerial, Integer.valueOf(cameraNo), "" + verifyCode);
                                    markers.add(markerInfo);

                                    Log.i("TAG", "ShipBean-----" + bean.getBoatDeviceId() + "----Device:" + deviceSerial);
                                }

                                addOverlay(markers);

                            }

                        }else{
                            Toast.makeText(MainDrawerActivity.this,response.getMessage(),Toast.LENGTH_SHORT).show();
                            isError=true;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        isError=true;
                    }
                }
        ){
            public Map<String, String> getHeaders() throws AuthFailureError {
                super.getHeaders();
                Map<String, String> header = new HashMap<>();
                header.put("token", App.token);

                return  header;
                //return super.getHeaders();
            }
        };

        App.getInstance().addRequest(request);


    }



    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理 
        mMapView.onPause();
    }
    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理 
        MapView.setMapCustomEnable(false);
        if (null != mMapView)
            // activity 销毁时同时销毁地图控件
            mMapView.onDestroy();
        if(null!=receiver)
            this.unregisterReceiver(receiver);

        if (App.getInstance().getSocketHelper().getSocket().connected()) {
            Log.i("TAG","Websocket-------reconnect");
            App.getInstance().getSocketHelper().getSocket().disconnect();
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
                Toast.makeText(MainDrawerActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
            }

        }
    }


    public void initMap() {

        mMapView=(MapView) findViewById(R.id.mmap);
        // 不显示缩放比例尺
        mMapView.showZoomControls(false);
        // 不显示百度地图Logo
        mMapView.removeViewAt(1);
        //百度地图
        mBaiduMap = mMapView.getMap();
        // 改变地图状态
        MapStatus mMapStatus = new MapStatus.Builder().zoom(10).build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        mBaiduMap.setMapStatus(mMapStatusUpdate);
        //设置地图状态改变监听器
        mBaiduMap.setOnMapStatusChangeListener(
                new BaiduMap.OnMapStatusChangeListener() {
                    @Override
                    public void onMapStatusChangeStart(MapStatus mapStatus) { }
                    @Override
                    public void onMapStatusChangeStart(MapStatus mapStatus, int i) { }
                    @Override
                    public void onMapStatusChange(MapStatus mapStatus) {
                        //当地图状态改变的时候，获取放大级别
                        zoomLevel = mapStatus.zoom;
                    }
                    @Override
                    public void onMapStatusChangeFinish(MapStatus mapStatus) { }
                }


        );

        //地图点击事件
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public boolean onMapPoiClick(MapPoi arg0) {
                return false;
            }

            @Override
            public void onMapClick(LatLng arg0) {
                //rl_marker.setVisibility(View.GONE);
            }
        });

        mBaiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {

            @Override
            public void onMapLoaded() {
                // TODO Auto-generated method stub

                Log.i("TAG","地图设置-------"+width+"------"+height);
                mMapView.setScaleControlPosition(new Point(width-130, height-130));
            }
        });
        //定位客户端的设置
        mLocationClient = new LocationClient(this);
        mLocationListener = new MyLocationListener();
        //注册监听
        mLocationClient.registerLocationListener(mLocationListener);
        //配置定位
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");//坐标类型
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//打开Gps
        option.setScanSpan(1000);//1000毫秒定位一次
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到

        mLocationClient.setLocOption(option);


        mMapView.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 介绍获取比例尺的宽高，需在MapView绘制完成之后
                int scaleControlViewWidth = mMapView.getScaleControlViewWidth();
                int scaleControlViewHeight = mMapView.getScaleControlViewHeight();
                Log.i("TAG","Width:"+scaleControlViewWidth+"---Height:"+scaleControlViewHeight);
            }
        }, 0);

    }

    //自定义的定位监听
    private class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //将获取的location信息给百度map
            MyLocationData data = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360 
                    //.direction(mLastX)
                    .latitude(location.getLatitude()).longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(data);
            //更新经纬度
            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();
            //配置定位图层显示方式，使用自己的定位图标
            MyLocationConfiguration configuration = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL
                    , true, bitmapDescriptor);
            mBaiduMap.setMyLocationConfigeration(configuration);
            if (mLatitude!=4.9E-324&&isFirstLocation) {
                //获取经纬度

                LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());

                MapStatusUpdate status = MapStatusUpdateFactory.newLatLng(ll);
                if (isError){
                    //mBaiduMap.setMapStatus(status);//直接到中间
                    mBaiduMap.animateMapStatus(status);//动画的方式到中间
                }
                mLatitude = location.getLatitude();
                mLongitude = location.getLongitude();
                isFirstLocation = false;
                Log.i("TAG", "位置：" + location.getAddrStr() + "****" + mLatitude + "---" + location.getLatitude() + "****" + mLongitude + "-----" + location.getLongitude());
                //showInfo("位置：" + location.getAddrStr());


            }

        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE,
                },
                88);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 88) {
            if ((//grantResults.length == 3 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                            && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                initMap();
            } else {
                Log.e(TAG, "We highly recommend that you need to grant the special permissions before initializing the SDK, otherwise some "
                        + "functions will not work");
                Toast.makeText(MainDrawerActivity.this,"We highly recommend that you need to grant the special permissions before initializing the SDK, otherwise some "
                        + "functions will not work",Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //显示marker
    private void addOverlay(final List<MarkerInfoUtil> infos) {
        //清空地图
        mBaiduMap.clear();
        //创建marker的显示图标
        final BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.bg_iv_ship_small);
        final BitmapDescriptor bitmap_large = BitmapDescriptorFactory.fromResource(R.mipmap.bg_iv_ship_big);
        LatLng latLng = null;
        final List<Marker> markerList = new ArrayList<>();

        Marker marker;
        OverlayOptions options = null;

        for (MarkerInfoUtil info : infos) {
            //获取经纬度
            latLng = new LatLng(info.getLatitude(), info.getLongitude());
            //设置marker

            options = new MarkerOptions()
                    .position(latLng)//设置位置
                    .icon(bitmap_large)//设置图标样式
                    .zIndex(9) // 设置marker所在层级
                    .draggable(true)//.animateType(MarkerOptions.MarkerAnimateType.drop)
            ; // 设置手势拖拽;*/

            //添加marker
            marker = (Marker) mBaiduMap.addOverlay(options);
            markerList.add(marker);



            //使用marker携带info信息，当点击事件的时候可以通过marker获得info信息
            Bundle bundle = new Bundle();
            //info必须实现序列化接口
            bundle.putSerializable("info", info);
            marker.setExtraInfo(bundle);
        }
        //将地图显示在最后一个marker的位置
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMap.setMapStatus(msu);
        //添加marker点击事件的监听
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //从marker中获取info信息
                final Bundle bundle = marker.getExtraInfo();
                MarkerInfoUtil infoUtil = (MarkerInfoUtil) bundle.getSerializable("info");

                /*for (int j = 0; j < markerList.size(); j++) {
                    markerList.get(j).setIcon(bitmap_large);
                }*/

                //marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.bg_iv_ship_big));
                //initInfoWindow(infoUtil);
                /**
                 * 开启缩放动画
                 */

            /*marker.setAnimation(getScaleAnimation());
             marker.startAnimation();*/
                //if(App.isConnect) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent ii = new Intent(MainDrawerActivity.this,MainDisplayActivity.class);
                        ii.putExtras(bundle);
                        startActivity(ii);
                    }
                },500);

               /* }else{
                    showToast("巡查船连接失败!");
                }*/


                return true;

            }

        });

    }


    /**
     * 创建缩放动画
     */
    private Animation getScaleAnimation() {
        ScaleAnimation mScale = new ScaleAnimation(1f, 2f, 1f);
        mScale.setDuration(2000);
        mScale.setRepeatMode(Animation.RepeatMode.RESTART);//动画重复模式
        mScale.setRepeatCount(1);//动画重复次数
        mScale.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart() {
            }

            @Override
            public void onAnimationEnd() {
            }

            @Override
            public void onAnimationCancel() {
            }

            @Override
            public void onAnimationRepeat() {
            }
        });

        return mScale;

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
                            Toast.makeText(MainDrawerActivity.this,response.getMessage(),Toast.LENGTH_SHORT).show();
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
    // 设置个性化地图config文件路径
    private void setMapCustomFile(Context context, String PATH) {
        FileOutputStream out = null;
        InputStream inputStream = null;
        String moduleName = null;
        try {
            inputStream = context.getAssets()
                    .open(PATH);
            byte[] b = new byte[inputStream.available()];
            inputStream.read(b);

            moduleName = context.getFilesDir().getAbsolutePath();
            File f = new File(moduleName + "/" + PATH);
            if (f.exists()) {
                f.delete();
            }
            f.createNewFile();
            out = new FileOutputStream(f);
            out.write(b);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        MapView.setCustomMapStylePath(moduleName + "/" + PATH);

    }
}
