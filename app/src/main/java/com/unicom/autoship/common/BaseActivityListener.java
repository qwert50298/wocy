
package com.unicom.autoship.common;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
/**
 * Created by dyzhou on 2018/7/28.
 */
public interface BaseActivityListener {

    /**
     * @return 首选项对象
     */
    public SharedPreferences getDefaultPref();

    /**
     * @return 上下文
     */
    public Context getContext();

    /**
     * @return 布局资源
     */
    public int rootViewRes();

    /**
     * @param msg 弹出消息
     */
    public void showToast(String msg);

    /**
     * @param msg 加载信息
     */
    public void showProgressDialog(String msg);

    /**
     * 弹层
     */
    public void showAlertDialog(String title, String message,
                                String positiveValue, DialogInterface.OnClickListener linstener);

    /**
     * 弹层
     *
     * @param title
     * @param content
     * @param positiveValue
     * @param positiveListener
     * @param nagetiveValue
     * @param nagetiveListener
     */
    public void showChoiceDialog(String title, String content,
                                 String positiveValue,
                                 DialogInterface.OnClickListener positiveListener,
                                 String nagetiveValue,
                                 DialogInterface.OnClickListener nagetiveListener);

    /**
     * @param title
     * @param items
     * @param listener
     */
    public void showItemsDialog(String title, String[] items,
                                OnClickListener listener);

    /**
     * 隐藏弹层
     */
    public void hideProgressDialog();

    /**
     * 在onCreate执行
     */
    public void doOnCreate(Bundle savedInstanceState);

    /**
     * 隐藏软键盘
     */
    public void hideSoftKeyboard();

    /**
     * Http接口返回
     * 
     * @param cmd
     * @param json
     */
    public void onHttpResponse(int cmd, int arg1, String json);

    /**
     * 查询控件
     * 
     * @param id
     * @return
     */
    public <V extends View> V findView(int id);

    public Handler getHandler();

    public <V extends View> V inflateView(int layout);
    

}
