package com.unicom.autoship.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.View;

import com.unicom.autoship.activity.BaseActivity;
import com.unicom.autoship.activity.LoginActivity;
import com.unicom.autoship.bean.ResponseBean;
import com.unicom.autoship.ui.dialoglikeios.DialogClickListener;
import com.unicom.autoship.utils.JsonUtil;


@SuppressLint("Override")
public abstract class BaseFragment extends Fragment implements
		BaseFragmentListener {
	private BaseFragmentHelper baseActivityHelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		baseActivityHelper = new BaseFragmentHelper(this, this);
		doOnCreate(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		baseActivityHelper.doOnDestory();
	}

	@Override
	public void showProgressDialog(String msg) {
		baseActivityHelper.showProgressDialog(msg);
	}

	@Override
	public void showAlertDialog(String title, String message,
                                String positiveValue, DialogInterface.OnClickListener linstener) {
		baseActivityHelper.showAlertDialog(title, message, positiveValue,
				linstener);
	}


	public void showIosAlertDialog (String message,
									String positiveValue) {
		baseActivityHelper.showIosAlertDialog(message, null, null, positiveValue, null);
	}

	public void showIosChoiceDialog (String message,
									 String negativeValue,
									 String positiveValue,
									 DialogClickListener listener2) {
		baseActivityHelper.showIosAlertDialog(message, negativeValue, null, positiveValue, listener2);
	}

	@Override
	public void showChoiceDialog(String title, String content,
                                 String positiveValue, OnClickListener positiveListener,
                                 String nagetiveValue, OnClickListener nagetiveListener) {
		baseActivityHelper.showChoiceDialog(title, content, positiveValue,
				positiveListener, nagetiveValue, nagetiveListener);
	}

	@Override
	public void showItemsDialog(String title, String[] items,
                                OnClickListener listener) {
		baseActivityHelper.showItemsDialog(title, items, listener);
	}

	@Override
	public void hideProgressDialog() {
		baseActivityHelper.hideProgressDialog();
	}

	@Override
	public SharedPreferences getDefaultPref() {
		return baseActivityHelper.getDefaultPref();
	}

	@Override
	public void doOnCreate(Bundle savedInstanceState) {
		baseActivityHelper.doOnCreate(savedInstanceState);
	}

	@Override
	public Context getContext() {
		return baseActivityHelper.getContext();
	}

	@Override
	public void showToast(String msg) {
		baseActivityHelper.showToast(msg);
	}

	@Override
	public <V extends View> V findView(int id) {
		return baseActivityHelper.findView(id);
	}

	@Override
	public void onHttpResponse(int cmd, String json) {
		/*ResponseBean responseBean = JsonUtil.objectFromJson(json, ResponseBean.class);
		if (responseBean.isSuccess() && responseBean.getEcode() == SystemCode.ECODE_NEED_RESIGN) {

			((BaseActivity)getActivity()).showIosOneChoiceDialog("用户授权过期，您需要重新登录。", "重新登录", new DialogClickListener() {
				@Override
				public void onClick(View v) {
					super.onClick(v);
					gotoLogin();

				}
			});
			return;
		}*/
	}

	private void gotoLogin() {
		Intent intent = new Intent(getActivity(), LoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		getActivity().finish();
	}

	@Override
	public Handler getHandler() {
		return baseActivityHelper.getHandler();
	}

	@Override
	public <V extends View> V inflateView(int layout) {
		return baseActivityHelper.inflateView(layout);
	}

}
