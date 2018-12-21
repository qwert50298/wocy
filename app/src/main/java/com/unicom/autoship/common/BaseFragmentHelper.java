package com.unicom.autoship.common;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;

import com.unicom.autoship.ui.dialoglikeios.DialogClickListener;
import com.unicom.autoship.ui.dialoglikeios.DialogLikeIOS;
import com.unicom.autoship.utils.ToastUtils;


public class BaseFragmentHelper implements BaseFragmentListener {
	private Fragment mFragment;// 上下文
	private ProgressDialog mDialog;// 网络加载
	private BaseFragmentListener listener;
	private Handler handler;
	private LayoutInflater layoutInflater;
	//private TitleBar titleBar;

	public BaseFragmentHelper(Fragment fragment, BaseFragmentListener listener) {
		this.mFragment = fragment;
		this.listener = listener;
	}

	@Override
	public void showProgressDialog(String msg) {
		// 如果已经存在进度条并且该进度条处于显示状态，将取消该进度条的显示。
		if (mDialog != null && mDialog.isShowing()) {
			hideProgressDialog();
		}

		mDialog = new ProgressDialog(mFragment.getActivity());
		mDialog.setMessage(msg);
		mDialog.setIndeterminate(true);
		mDialog.setCancelable(false);
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.show();
	}

	@Override
	public void showAlertDialog(String title, String message,
                                String positiveValue, OnClickListener linstener) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(
				mFragment.getActivity()).setMessage(message);
		dialog.setTitle(title);
		dialog.setPositiveButton(positiveValue, linstener);
		dialog.show();
	}

	@Override
	public void showChoiceDialog(String title, String content,
                                 String positiveValue, OnClickListener positiveListener,
                                 String nagetiveValue, OnClickListener nagetiveListener) {
		AlertDialog.Builder budiler = new AlertDialog.Builder(
				mFragment.getActivity());
		budiler.setTitle(title);
		budiler.setMessage(content);
		budiler.setPositiveButton(positiveValue, positiveListener);
		budiler.setNegativeButton(nagetiveValue, nagetiveListener);
		budiler.create().show();
	}


	public void showIosAlertDialog (String message,
									String negativeValue,
									DialogClickListener listener1,
									String positiveValue,
									DialogClickListener listener2) {
		DialogLikeIOS dialogLikeIOS = new DialogLikeIOS(mFragment.getActivity(), message, negativeValue, positiveValue);
		if (null != negativeValue) {
			dialogLikeIOS.setNegativeOnClickListener(listener1);
		}
		dialogLikeIOS.setPositiveOnClickListener(listener2);
		dialogLikeIOS.show();
	}

	@Override
	public void showItemsDialog(String title, String[] items,
                                OnClickListener listener) {
		AlertDialog.Builder budiler = new AlertDialog.Builder(
				mFragment.getActivity());
		budiler.setTitle(title);
		budiler.setItems(items, listener);
		budiler.setNegativeButton("取消", null);
		budiler.create().show();
	}

	@Override
	public void hideProgressDialog() {
		try {
			mDialog.dismiss();
			mDialog = null;
		} catch (Exception e) {
		}
	}

	@Override
	public SharedPreferences getDefaultPref() {
		return PreferenceManager.getDefaultSharedPreferences(mFragment.getActivity());
	}

	@Override
	public void doOnCreate(Bundle savedInstanceState) {
		//mInitTitleBar();
	}

	public void doOnDestory() {
		hideProgressDialog();
	}

	@SuppressLint("HandlerLeak")
	private void initHandler() {
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				int cmd = msg.what;
				String json = (String) msg.obj;
				onHttpResponse(cmd, json);
			}
		};
	}

	@Override
	public Handler getHandler() {
		if (handler == null)
			initHandler();
		return handler;
	}

	@Override
	public void onHttpResponse(int cmd, String json) {
		listener.onHttpResponse(cmd, json);
	}

	/**
	 * 初始化标题栏
	 */
	/*private void mInitTitleBar() {
		if (mFragment.getView().findViewById(R.id.titleBar) != null) {

			titleBar = initTitlebar();
			if (titleBar != null) {
				Button titlebarLeftButton = (Button) mFragment.getView()
						.findViewById(R.id.titlebarLeftButton);
				TextView titlebarTV = (TextView) mFragment.getView()
						.findViewById(R.id.titlebarTV);
				LinearLayout titlebarRightContainerLayout = (LinearLayout) mFragment
						.getView().findViewById(
								R.id.titlebarRightContainerLayout);

				titlebarLeftButton.setOnClickListener(titleBar
						.getmLeftBtnOnClickListener());
				titlebarTV.setText(titleBar.getmTitleValue());
				if (titleBar.getmRightContainerLayout() != null)
					titlebarRightContainerLayout.addView(titleBar
							.getmRightContainerLayout(),
							new LinearLayout.LayoutParams(
									LayoutParams.WRAP_CONTENT,
									LayoutParams.WRAP_CONTENT));
			} else {
				mFragment.getView().findViewById(R.id.titleBar)
						.setVisibility(View.GONE);
			}
		}
	}*/

	@Override
	public Context getContext() {
		return mFragment.getActivity();
	}

	@Override
	public void showToast(String msg) {
		ToastUtils.show(mFragment.getActivity(), msg);
	}

	@Override
	public int rootViewRes() {
		return listener.rootViewRes();
	}

/*	@Override
	public TitleBar initTitlebar() {
		return listener.initTitlebar();
	}*/

	@Override
	@SuppressWarnings("unchecked")
	public <V extends View> V findView(int id) {
		return (V) mFragment.getView().findViewById(id);
	}

//	public TextView getTitleTextView() {
//		return (TextView) mFragment.getView().findViewById(R.id.titlebarTV);
//	}

	@Override
	@SuppressWarnings("unchecked")
	public <V extends View> V inflateView(int layout) {
		if (layoutInflater == null) {
			layoutInflater = LayoutInflater.from(mFragment.getActivity());
		}
		return (V) layoutInflater.inflate(layout, null);
	}

}
