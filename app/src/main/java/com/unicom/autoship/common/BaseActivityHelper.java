package com.unicom.autoship.common;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.unicom.autoship.R;
import com.unicom.autoship.ui.dialoglikeios.DialogClickListener;
import com.unicom.autoship.ui.dialoglikeios.DialogLikeIOS;
import com.unicom.autoship.utils.ToastUtils;

/**
 * Created by dyzhou on 2018/7/28.
 */
public class BaseActivityHelper implements BaseActivityListener {
    private Activity mActivity;// 上下文
    private ProgressDialog mDialog;// 网络加载
    private BaseActivityListener listener;
    private Handler handler;
    private LayoutInflater layoutInflater;

    public BaseActivityHelper(Activity activity, BaseActivityListener listener) {
        this.mActivity = activity;
        this.listener = listener;
    }

    @Override
    public void showProgressDialog(String msg) {
        showProgressDialog(msg, false);
    }

    public void showProgressDialog(String msg, boolean cancelable) {
        // 如果已经存在进度条并且该进度条处于显示状态，将取消该进度条的显示。
        if (mActivity.isFinishing()) {
            return;
        }
        if (mDialog != null && mDialog.isShowing()) {
            hideProgressDialog();
        }
        mDialog = new ProgressDialog(mActivity);
        mDialog.setMessage(msg);
        mDialog.setCancelable(cancelable);
        mDialog.setIndeterminate(true);
        mDialog.setCanceledOnTouchOutside(false);
        //mDialog.setProgressDrawable(mActivity.getResources().getDrawable(R.drawable.load_msg_progress));
        mDialog.show();
    }

    @Override
    public void showAlertDialog(String title, String message,
                                String positiveValue, OnClickListener linstener) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity)
                .setMessage(message);
        dialog.setTitle(title);
        dialog.setPositiveButton(positiveValue, linstener);
        dialog.show();
    }

    DialogLikeIOS dialogLikeIOS;
    public void showIosAlertDialog (String message,
                                    String negativeValue,
                                    DialogClickListener listener1,
                                    String positiveValue,
                                    DialogClickListener listener2) {
        if (null != dialogLikeIOS && dialogLikeIOS.isShowing() && mActivity == null) {
            return;
        }
        dialogLikeIOS = new DialogLikeIOS(mActivity, message, negativeValue, positiveValue);
        if (null != negativeValue) {
            dialogLikeIOS.setNegativeOnClickListener(listener1);
        }
        dialogLikeIOS.setPositiveOnClickListener(listener2);
        dialogLikeIOS.show();
    }

    public void hideIosDialog () {
        if (null != dialogLikeIOS) {
            dialogLikeIOS.dismiss();
        }
    }

    public void showIosAlertDialogWithTitle (String message,
                                                String negativeValue,
                                                DialogClickListener listener1,
                                                String positiveValue,
                                                DialogClickListener listener2) {
        if (null != dialogLikeIOS && dialogLikeIOS.isShowing() && mActivity == null) {
            return;
        }
        dialogLikeIOS = new DialogLikeIOS(mActivity, message, negativeValue, positiveValue);
        if (null != negativeValue) {
            dialogLikeIOS.setNegativeOnClickListener(listener1);
        }
        dialogLikeIOS.setPositiveOnClickListener(listener2);
        dialogLikeIOS.show();
        dialogLikeIOS.findViewById(R.id.tv_ios_dialog_title).setVisibility(View.VISIBLE);
    }

    @Override
    public void showChoiceDialog(String title, String content,
                                 String positiveValue, OnClickListener positiveListener,
                                 String nagetiveValue, OnClickListener nagetiveListener) {

        AlertDialog.Builder budiler = new AlertDialog.Builder(mActivity);
        budiler.setTitle(title);
        budiler.setMessage(content);
        budiler.setPositiveButton(positiveValue, positiveListener);
        budiler.setNegativeButton(nagetiveValue, nagetiveListener);
        budiler.create().show();
    }

    @Override
    public void showItemsDialog(String title, String[] items,
                                OnClickListener listener) {
        AlertDialog.Builder budiler = new AlertDialog.Builder(mActivity);
        budiler.setTitle(title);
        budiler.setItems(items, listener);
        budiler.setNegativeButton("取消", null);
        budiler.create().show();
    }

    @Override
    public void hideProgressDialog() {
        if (mDialog == null) return;
        mDialog.dismiss();
        mDialog = null;
    }

    @Override
    public SharedPreferences getDefaultPref() {

        return PreferenceManager.getDefaultSharedPreferences(mActivity);
    }

    @Override
    public void doOnCreate(Bundle savedInstanceState) {
//         ActivityStack.getInstance().push(mActivity);
        if (listener.rootViewRes() != 0) {
            mActivity.setContentView(listener.rootViewRes());
        }
    }

    public void doOnDestroy() {
        hideProgressDialog();
//         ActivityStack.getInstance().remove(mActivity);
        if (handler != null)
            handler.removeCallbacksAndMessages(null);
        ToastUtils.clearToast();
    }

    @SuppressLint("HandlerLeak")
    private void initHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                int cmd = msg.what;
                int arg1 = msg.arg1;
                String json = (String) msg.obj;
                onHttpResponse(cmd, arg1, json);
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
    public void onHttpResponse(int cmd, int arg1, String json) {
        listener.onHttpResponse(cmd, arg1, json);
    }

    @Override
    public Context getContext() {
        return mActivity;
    }

    @Override
    public void showToast(String msg) {
        ToastUtils.show(mActivity, msg);
    }

    @Override
    public int rootViewRes() {
        return listener.rootViewRes();
    }

    @Override
    public void hideSoftKeyboard() {
        try {
            ((InputMethodManager) mActivity
                    .getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(mActivity.getCurrentFocus()
                                    .getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public <V extends View> V findView(int id) {
        return (V) mActivity.findViewById(id);
    }

    @Override
    public <V extends View> V inflateView(int layout) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(mActivity);
        }
        return (V) layoutInflater.inflate(layout, null);
    }

}
