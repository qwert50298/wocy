package com.unicom.autoship.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.unicom.autoship.App;
import com.unicom.autoship.MainActivity;
import com.unicom.autoship.R;
import com.unicom.autoship.bean.ResponseBean;
import com.unicom.autoship.bean.YsData;
import com.unicom.autoship.net.URL;
import com.unicom.autoship.utils.GsonRequest;
import com.unicom.autoship.utils.Utils;
import com.unicom.autoship.widget.loading.LoadingView;
import com.videogo.openapi.EZOpenSDK;

import java.util.HashMap;
import java.util.Map;

import static com.unicom.autoship.App.accessToken;
import static com.unicom.autoship.App.expireTime;

public class LoadingActivity extends BaseActivity {


    @Override
    public int rootViewRes() {
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_loading);


        autoLogin();
    }

    private void autoLogin() {

        boolean isOnline = getDefaultPref().getBoolean("isOnline",false);
        if (isOnline) {

            Log.i("TAG","TIME-----"+System.currentTimeMillis()+"---------"+App.expireTime);
            Log.i("TAG","---------"+App.accessToken);
            String accessToken = getDefaultPref().getString("token","");

            if (null != accessToken&&!"".equals(accessToken)) {

                long expireTime=getDefaultPref().getLong("tokenExpire",0L);
                Long time = System.currentTimeMillis();
                Log.i("TAG","TIME-----"+time+"---------"+App.expireTime);
                if (time <expireTime) {
                    Log.i("TAG","自动登录---Main");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent i = new Intent(LoadingActivity.this, MainDrawerActivity.class);
                            startActivity(i);
                            finish();
                        }
                    },600);

                } else{

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent i = new Intent(LoadingActivity.this, LoginActivity.class);
                            startActivity(i);
                            finish();
                        }
                    },600);

                }

            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(LoadingActivity.this, LoginActivity.class);
                        startActivity(i);
                        finish();
                    }
                },2000);
            }

        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(LoadingActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            },2000);
        }


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
                            Toast.makeText(LoadingActivity.this,response.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                    }
                }
        ); /*{
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("appKey",  App.APP_KEY);
                params.put("appSecret", App.APP_SECRET);
                return params;
            }
            *//*@Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("appKey", App.APP_KEY);
                header.put("appSecret", App.APP_SECRET);

                return super.getHeaders();
            }*//*
        };*/

        App.getInstance().addRequest(request);


    }
}
