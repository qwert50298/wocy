package com.unicom.autoship.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.tencent.bugly.beta.Beta;
import com.unicom.autoship.App;
import com.unicom.autoship.MainActivity;
import com.unicom.autoship.R;
import com.unicom.autoship.bean.LoginResp;
import com.unicom.autoship.bean.ResponseBean;
import com.unicom.autoship.bean.TokenBean;
import com.unicom.autoship.bean.YsData;
import com.unicom.autoship.net.URL;
import com.unicom.autoship.utils.GsonRequest;
import com.unicom.autoship.utils.Utils;
import com.videogo.openapi.EZOpenSDK;

import static android.Manifest.permission.READ_CONTACTS;
import static com.unicom.autoship.App.accessToken;
import static com.unicom.autoship.App.expireTime;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity{

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    //private View mProgressView;
    //private View mLoginFormView;

    @Override
    public int rootViewRes() {
        return R.layout.activity_login;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email);
        //populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

       // mLoginFormView = findViewById(R.id.login_form);
       // mProgressView = findViewById(R.id.login_progress);

        if (Build.VERSION.SDK_INT >= 23 &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED
                ) {
            requestPermission();
        } else {
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

            } else {
                Log.e(TAG, "We highly recommend that you need to grant the special permissions before initializing the SDK, otherwise some "
                        + "functions will not work");
                Toast.makeText(LoginActivity.this,"We highly recommend that you need to grant the special permissions before initializing the SDK, otherwise some "
                        + "functions will not work",Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
        }else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } /*else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }*/

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            boolean isNetworkEnable = Utils.isNetworkEnable(LoginActivity.this);
            if (isNetworkEnable != false) {
                showProgressDialog("登录中...");
                loginApp(email, password);
            }else {
                Toast.makeText(LoginActivity.this,"网络不可用",Toast.LENGTH_SHORT);
            }
            //showProgress(true);
            /*mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);*/
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    private void initGPS() {
        LocationManager locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);
        // 判断GPS模块是否开启，如果没有则跳转至设置开启界面，设置完毕后返回到首页
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("提醒：");
            dialog.setMessage("为了更好的为您服务，请您打开您的GPS!");
            dialog.setCancelable(false);
            //界面上左边按钮，及其监听
            dialog.setNeutralButton("确定",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                            // 转到手机设置界面，用户设置GPS
                            Intent intent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, 0); // 设置完成后返回到原来的界面

                        }
                    });
            //界面上右边按钮，及其监听
            dialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    arg0.dismiss();
                }
            } );
            dialog.show();
        } else {
        }
    }

    public void loginApp(String user, String pswd) {
        Map<String, String> params = new HashMap<>();
        params.put("username", user);
        params.put("password", pswd);
        String url = URL.LOGIN_URL;
        GsonRequest<LoginResp> request = new GsonRequest<LoginResp>(
                url,
                LoginResp.class,
                params,
                new Response.Listener<LoginResp>() {
                    @Override
                    public void onResponse(LoginResp response) {
                        Log.i("TAG","LoginResponse-----"+response.toString());
                        hideProgressDialog();

                        if(response.isSuccess()){

                            LoginResp.LoginData data = response.getData();

                            List<TokenBean> tokenBeans = data.getTokens();
                            App.token=data.getToken();
                            App.tokenExpire=data.getTokenExpire();
                            for(TokenBean bean:tokenBeans) {
                                if(null!=bean.getAccessTokenKey()&&"ys".equals(bean.getAccessTokenKey())) {
                                    App.accessToken = bean.getAccessToken();
                                    App.expireTime = bean.getAccessTokenExpire();
                                    App.refreshToken = bean.getAccessTokenKey();
                                }else if(null!=bean.getAccessTokenKey()&&"usr".equals(bean.getAccessTokenKey())) {
                                    App.account = bean.getAccessApp();
                                    App.password = bean.getAccessToken();

                                }
                            }
                            Log.i("TAG","accessToken-----"+accessToken+"----expireTime---"+expireTime);
                            EZOpenSDK.getInstance().setAccessToken(App.accessToken);
                            //SharedPreferences.Editor editor= sp.edit();
                            SharedPreferences.Editor editor = getDefaultPref().edit();
                            editor.putString("accessToken", App.accessToken);
                            editor.putLong("expireTime", App.expireTime);
                            editor.putString("token",App.token);
                            editor.putString("refreshToken",App.refreshToken);
                            editor.putString("account",App.account);
                            editor.putString("pswd",App.password);
                            editor.putLong("tokenExpire",App.tokenExpire);
                            App.isOnline=true;
                            editor.putBoolean("isOnline", true);
                            boolean isOK=editor.commit();

                            Log.i("TAG","RESULT-----"+isOK);
                            Intent i = new Intent(LoginActivity.this, MainDrawerActivity.class);
                            startActivity(i);
                            finish();

                        }else{
                            mPasswordView.setError(response.getMessage());
                            mPasswordView.requestFocus();
                            Toast.makeText(LoginActivity.this,response.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        hideProgressDialog();
                    }
                }
        ) ;

        App.getInstance().addRequest(request);


    }

    /*public void getToken() {
        Map<String, String> params = new HashMap<>();
        params.put("appKey", App.APP_KEY);
        params.put("appSecret", App.APP_SECRET);
        String url = App.YS_TOKEN_URL;
        GsonRequest<ResponseBean> request = new GsonRequest<ResponseBean>(
                url,
                ResponseBean.class,
                params,
                new Response.Listener<ResponseBean>() {
                    @Override
                    public void onResponse(ResponseBean response) {
                        Log.i("TAG","Response-----"+response.toString());

                        if("200".equals(response.getCode())){

                            YsData data = response.getData();
                            App.accessToken=data.getAccessToken();
                            App.expireTime=data.getExpireTime();
                            Log.i("TAG","accessToken-----"+accessToken+"----expireTime---"+expireTime);
                            EZOpenSDK.getInstance().setAccessToken(App.accessToken);
                            //SharedPreferences.Editor editor= sp.edit();
                            SharedPreferences.Editor editor = getDefaultPref().edit();
                            editor.putString("accessToken", App.accessToken);
                            editor.putLong("expireTime", App.expireTime);
                            boolean isOK=editor.commit();
                            Log.i("TAG","RESULT-----"+isOK);
                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(i);
                            finish();

                        }else{
                            Toast.makeText(LoginActivity.this,response.getMsg(),Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                    }
                }
        ) {
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
        };

        App.getInstance().addRequest(request);


    }*/

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }





    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            //showProgress(false);
            hideProgressDialog();

            if (success) {



            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            hideProgressDialog();
            //showProgress(false);
        }
    }
}

