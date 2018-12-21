package com.unicom.autoship.utils;

import android.content.Context;
import android.widget.Toast;
/**
 * Created by dyzhou on 2018/8/1.
 */
public class ToastUtils {
	private static Toast toast;

	public static void show(Context context, int res) {
		if (toast == null) {
			toast = Toast.makeText(context, res, Toast.LENGTH_SHORT);
		} else {
			toast.setText(res);
		}
		toast.show();
	}

	public static void show(Context context, String res) {
		if (toast == null) {
			toast = Toast.makeText(context, res, Toast.LENGTH_SHORT);
		} else {
			toast.setText(res);
		}
		toast.show();
	}
	
	public static void showLong(Context context, String res) {
		if (toast == null) {
			toast = Toast.makeText(context, res, Toast.LENGTH_LONG);
		} else {
			toast.setText(res);
		}
		toast.show();
	}

	public static void clearToast () {
		toast = null;
	}
}
