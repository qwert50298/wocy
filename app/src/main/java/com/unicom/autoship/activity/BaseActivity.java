package com.unicom.autoship.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.unicom.autoship.App;
import com.unicom.autoship.common.BaseActivityHelper;
import com.unicom.autoship.common.BaseActivityListener;
import com.unicom.autoship.ui.dialoglikeios.DialogClickListener;
import com.usr.usrcloudmqttsdkdemo.base.UsrBaseActivity;

public abstract class BaseActivity extends AppCompatActivity implements BaseActivityListener {
    private BaseActivityHelper baseActivityHelper;
    /**
     * 是否沉浸状态栏
     **/
    private boolean isSetStatusBar = true;
    /**
     * 是否允许全屏
     **/
    private boolean mAllowFullScreen = true;
    /**
     * 是否禁止旋转屏幕
     **/
    private boolean isAllowScreenRoate = false;
    /**
     * 日志输出标志
     **/
    protected final String TAG = this.getClass().getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "BaseActivity-->onCreate()");
        getHelper();
        doOnCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private BaseActivityHelper getHelper() {
        if (baseActivityHelper == null) {
            baseActivityHelper = new BaseActivityHelper(this, this);
        }
        return baseActivityHelper;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getHelper().doOnDestroy();
        baseActivityHelper = null;
        App.getRefWatcher().watch(BaseActivity.this);
    }

    @Override
    public void showProgressDialog(String msg) {
        getHelper().showProgressDialog(msg,false);
    }

    public void showProgressDialog(String msg, boolean cancleable) {
        getHelper().showProgressDialog(msg,cancleable);
    }

    @Override
    public void showAlertDialog(String title, String message,
                                String positiveValue, DialogInterface.OnClickListener linstener) {
        getHelper().showAlertDialog(title, message, positiveValue,
                linstener);
    }

    public void showIosAlertDialog (String message,
                                    String positiveValue) {
        getHelper().showIosAlertDialog(message, null, null, positiveValue, null);
    }

    public void showIosDialogWithTitle (String message,
                                        String positiveValue) {
        getHelper().showIosAlertDialogWithTitle(message, null, null, positiveValue, null);
    }

    public void showIosOneChoiceDialog (String message,
                                        String positiveValue,
                                        DialogClickListener listener) {
        getHelper().showIosAlertDialog(message, null, null, positiveValue, listener);
    }

    public void showIosChoiceDialog (String message,
                                     String negativeValue,
                                     String positiveValue,
                                     DialogClickListener listener2) {
        getHelper().showIosAlertDialog(message, negativeValue, null, positiveValue, listener2);
    }

    public void hideIosDialog() {
        getHelper().hideIosDialog();
    }

    @Override
    public void showChoiceDialog(String title, String content,
                                 String positiveValue, DialogInterface.OnClickListener positiveListener,
                                 String nagetiveValue, DialogInterface.OnClickListener nagetiveListener) {
        getHelper().showChoiceDialog(title, content, positiveValue,
                positiveListener, nagetiveValue, nagetiveListener);
    }

    @Override
    public void showItemsDialog(String title, String[] items,
                                DialogInterface.OnClickListener listener) {
        getHelper().showItemsDialog(title, items, listener);
    }

    @Override
    public void hideProgressDialog() {
        getHelper().hideProgressDialog();
    }

    @Override
    public SharedPreferences getDefaultPref() {
        return getHelper().getDefaultPref();
    }

    @Override
    public void doOnCreate(Bundle savedInstanceState) {
        getHelper().doOnCreate(savedInstanceState);
    }

    @Override
    public Context getContext() {
        return getHelper().getContext();
    }

    @Override
    public void showToast(String msg) {
        getHelper().showToast(msg);
    }

    @Override
    public void hideSoftKeyboard() {
        getHelper().hideSoftKeyboard();
    }

    @Override
    public <V extends View> V findView(int id) {
        return getHelper().findView(id);
    }

    public void onHttpResponse(int cmd, String json) {
    }

    @Override
    public void onHttpResponse(int cmd,int arg1, String json) {
       /* ResponseBean responseBean = JsonUtil.objectFromJson(json, ResponseBean.class);
        if (null != responseBean
                && responseBean.getEcode() == SystemCode.ECODE_NEED_RESIGN) {
            hideProgressDialog();
            showIosOneChoiceDialog("用户授权过期，您需要重新登录。", "重新登录", new DialogClickListener() {
                @Override
                public void onClick(View v) {
                    super.onClick(v);
                    gotoLogin();
                }
            });
            return;
        }*/
        onHttpResponse(cmd, json);
    }

    private void gotoLogin() {

    }

    @Override
    public Handler getHandler() {
        return getHelper().getHandler();
    }

    @Override
    public <V extends View> V inflateView(int layout) {
        return getHelper().inflateView(layout);
    }

    /**
     * [初始化参数]
     */
    public void initParms() {

    }

    /**
     * [初始化控件]
     */
    public void initView() {

    }

    /**
     * [设置监听]
     */
    public void setListener() {

    }

    /**
     * [页面跳转]
     *
     * @param clz
     */
    public void startActivity(Class<?> clz) {
        startActivity(new Intent(BaseActivity.this, clz));
    }
    /**
     * [启动服务]
     *
     * @param clz
     */
    public void startService(Class<?> clz) {
        startService(new Intent(BaseActivity.this, clz));
    }
    /**
     * [携带数据的启动服务]
     *
     * @param clz
     */
    public void startServiceWithParm(Class<?> clz, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startService(intent);
    }

    /**
     * [携带数据的页面跳转]
     *
     * @param clz
     * @param bundle
     */
    public void startActivityWithParm(Class<?> clz, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * [是否允许全屏]
     *
     * @param allowFullScreen
     */
    public void setAllowFullScreen(boolean allowFullScreen) {
        this.mAllowFullScreen = allowFullScreen;
    }

    /**
     * [是否设置沉浸状态栏]
     *
     * @param isSetStatusBar
     */
    public void setSteepStatusBar(boolean isSetStatusBar) {
        this.isSetStatusBar = isSetStatusBar;
    }

    /**
     * [是否允许屏幕旋转]
     *
     * @param isAllowScreenRoate
     */
    public void setScreenRoate(boolean isAllowScreenRoate) {
        this.isAllowScreenRoate = isAllowScreenRoate;
    }


    /**
     * [简化Snackbar]
     *
     * @param msg
     */
    public void showSnackbar(View view, String msg) {
        Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show();
    }

}