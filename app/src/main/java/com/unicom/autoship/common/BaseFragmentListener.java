package com.unicom.autoship.common;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;



/**
 * Fragment Listener基类
 * 
 * @author DYZHOU
 * 
 */
public interface BaseFragmentListener {

	/**
	 * 首选项对象
	 */
	public SharedPreferences getDefaultPref();

	/**
	 * 上下文
	 */
	public Context getContext();

	/**
	 * 布局资源
	 */
	public int rootViewRes();

	/**
	 * 弹出消息
	 */
	public void showToast(String msg);

	/**
	 * 加载信息
	 */
	public void showProgressDialog(String msg);

	/**
	 * 弹层
	 * 
	 */
	public void showAlertDialog(String title, String message,
                                String positiveValue, DialogInterface.OnClickListener linstener);

	/**
	 * 弹层
	 *
	 */
	public void showChoiceDialog(String title, String content,
                                 String positiveValue,
                                 DialogInterface.OnClickListener positiveListener,
                                 String nagetiveValue,
                                 DialogInterface.OnClickListener nagetiveListener);

	/**
     *
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

/*	*//**
	 * 标题栏
	 *//*
	public TitleBar initTitlebar();*/

	/**
	 * Http接口返回
	 */
	public void onHttpResponse(int cmd, String json);

	/**
	 * 查询控件
	 */
	public <V extends View> V findView(int id);

	public Handler getHandler();

	public <V extends View> V inflateView(int layout);

}
