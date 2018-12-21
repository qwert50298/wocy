
package com.unicom.autoship.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


import com.unicom.autoship.App;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
/**
 * Created by dyzhou on 2018/8/1.
 */
public class Utils {
	public static final String BASE_PATH = Environment.getExternalStorageDirectory() + "/autoship";
	public static final String IMAGE_PATH = BASE_PATH + "/image/";
	public static final String BL_PATH = BASE_PATH + "/bl/";
	public static final String IMAGE_EBOOK_PATH = BASE_PATH + "/image/ebook/";
	public static final String PHONE_NUMBER_REGX = "^(13[0-9]|15[012356789]|17[0678]|18[0-9]|14[57])[0-9]{8}$";


	/**
	 * @return SD卡是否存在
	 */
	public static boolean isSdExist() {
		return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	}

	/**
	 * 将request请求参数装换成http请求类型
	 * 
	 * @param parameters
	 * @return
	 */
	public static String encodeBodyParameters(HttpParameters parameters) {
		if (parameters == null || parameters.size() == 0) {
			return "";
		}
		String resultStr = parameters.getJsonStr();
		return resultStr;
	}

	/**
	 * 将请求参数拼接到URL
	 * @param UrlStr
	 * @param parameters
     * @return
     */
	public static String enCodePathParameters(String UrlStr, HttpParameters parameters) {
		if (parameters == null || parameters.size() == 0) {
			return UrlStr;
		}
		return parameters.getUrlStrWithParams(UrlStr);
	}


	/**
	 * 工具类，读取文件二进制数据
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static byte[] fileToByteArray(File file) {
		try {
			return streamToByteArray(new FileInputStream(file));
		} catch (FileNotFoundException e) {

			return null;
		}
	}

	/**
	 * 工具，将输入流转换成字节数组
	 * 
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	public static byte[] streamToByteArray(InputStream inputStream) {
		byte[] content = null;

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		BufferedInputStream bis = new BufferedInputStream(inputStream);

		try {
			byte[] buffer = new byte[1024];
			int length = 0;
			while ((length = bis.read(buffer)) != -1) {
				baos.write(buffer, 0, length);
			}

			content = baos.toByteArray();
			if (content.length == 0) {
				content = null;
			}

			baos.close();
			bis.close();
		} catch (IOException e) {

		} finally {
			if (baos != null) {
				try {
					baos.close();
				} catch (IOException e) {

				}
			}
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {

				}
			}
		}

		return content;
	}

	/**
	 * @param context
	 * @return 网络是否可用
	 */
	public static boolean isNetworkEnable(final Context context) {
		boolean isNetworkEnable = false;
		if (context != null) {
			final ConnectivityManager manager = (ConnectivityManager) context.getApplicationContext()
					.getSystemService(Context.CONNECTIVITY_SERVICE);

			if (manager != null) {
				NetworkInfo networkinfo = manager.getActiveNetworkInfo();
				isNetworkEnable = (networkinfo != null
						&& networkinfo.isAvailable() // 已经保存
						&& networkinfo.isConnected()); // 已经连接
			}
		}
		return isNetworkEnable;
	}

	/**
	 * 按钮点击保护，防止用户点击多次
	 * 
	 * @param v
	 *            需要保护的view
	 * @param delay
	 *            下一次可点击的延时
	 */
	public static void clickProtect(final View v, long delay) {
		v.setClickable(false);
		v.postDelayed(new Runnable() {

			@Override
			public void run() {
				v.setClickable(true);
			}
		}, delay);
	}

	/**
	 * 返回当前程序版本名
	 */
	public static String getAppVersionName(String prefix) {
		String versionName = "";
		try {
			PackageManager pm = App.getInstance().getPackageManager();
			PackageInfo pi = pm.getPackageInfo(App.getInstance().getPackageName(), 0);
			versionName = prefix + pi.versionName;
			if (versionName == null || versionName.length() <= 0) {
				return "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return versionName;
	}

	/**
	 * 动态隐藏软键盘
	 * @param activity
	 */
	@TargetApi(Build.VERSION_CODES.CUPCAKE)
	public static void hideSoftInput(Activity activity) {
		View view = activity.getWindow().peekDecorView();
		if (view != null) {
			InputMethodManager inputmanger = (InputMethodManager) activity
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	/**
	 * 动态隐藏软键盘
	 * @param context
	 * @param edit
	 */
	public static void hideSoftInput(Context context, EditText edit) {
		edit.clearFocus();
		InputMethodManager inputmanger = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputmanger.hideSoftInputFromWindow(edit.getWindowToken(), 0);
	}

	public static void copyBlLibFile( final String targetPath){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String[] list = App.getInstance().getAssets().list("bl");
                    if (list == null || list.length < 1) return;
                    for (String fileName : list){
                        File t = new File(targetPath, fileName);

                        if (t.exists()) continue;
                        new File(targetPath).mkdirs();
                        FileOutputStream outputStream = new FileOutputStream(t.getAbsolutePath());
                        InputStream inputStream = App.getInstance().getAssets().open("bl/" + fileName);
                        byte[] buff = new byte[2048];
                        int len = 0;
                        while ((len = inputStream.read(buff)) != -1){
                            outputStream.write(buff, 0, len);
                        }
                        inputStream.close();
                        outputStream.flush();
                        outputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
//                    FIR.sendCrashManually(e);
                }
            }
        }).start();
    }

	// 判断是否是华为手机
	public static boolean isHuaWeiDevice() {

		final String HUAWEI = "huawei";
		if (Build.MODEL.toLowerCase().equals(HUAWEI)
				|| Build.FINGERPRINT.toLowerCase().startsWith(HUAWEI)
				|| Build.MANUFACTURER.toLowerCase().equals(HUAWEI)
				|| Build.BRAND.toLowerCase().equals(HUAWEI)) {
			return true;
		}

		// 华为的判断方式
		String emuiVerion = "";
		Class<?>[] clsArray = new Class<?>[] { String.class };
		Object[] objArray = new Object[] { "ro.build.version.emui" };
		try {
			Class<?> SystemPropertiesClass = Class
					.forName("android.os.SystemProperties");
			Method get = SystemPropertiesClass.getDeclaredMethod("get",
					clsArray);
			String version = (String) get.invoke(SystemPropertiesClass,
					objArray);
			Log.d("isHuaWeiDevice", "get EMUI version is:" + version);
			if (!TextUtils.isEmpty(version)) {
				return true;
			}
		} catch (ClassNotFoundException e) {
			Log.e("isHuaWeiDevice", " isHuaWeiDevice wrong, ClassNotFoundException");
		} catch (LinkageError e) {
			Log.e("isHuaWeiDevice", " isHuaWeiDevice wrong, LinkageError");
		} catch (NoSuchMethodException e) {
			Log.e("isHuaWeiDevice", " isHuaWeiDevice wrong, NoSuchMethodException");
		} catch (NullPointerException e) {
			Log.e("isHuaWeiDevice", " isHuaWeiDevice wrong, NullPointerException");
		} catch (Exception e) {
			Log.e("isHuaWeiDevice", " isHuaWeiDevice wrong");
		}
		return TextUtils.isEmpty(emuiVerion) ? false : true;
	}

	public static boolean isXiaoMiDevice () {
		final String XIAOMI = "xiaomi";
		if (Build.MODEL.toLowerCase().contains("mi")
				|| Build.FINGERPRINT.toLowerCase().startsWith(XIAOMI)
				|| Build.MANUFACTURER.toLowerCase().equals(XIAOMI)
				|| Build.BRAND.equals(XIAOMI)) {
			return true;
		}
		return false;
	}

	public static SharedPreferences getSpf (Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}

	/*public static int dip2px(Context context, float dpValue){
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue*scale +0.5f);
	}*/

	/**
	 * 将px值转换为sp值，保证文字大小不变
	 *
	 * @param pxValue
	 * @return
	 */
	public static int px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	/**
	 * dip转换px
	 */
	public static int dip2px(Context context, float dip) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dip * scale + 0.5f);
	}

	/**
	 * px转dp
	 *
	 * @param context
	 * @param pxVal
	 * @return
	 */
	public static float px2dp(Context context, float pxVal) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (pxVal / scale);
	}

	/**
	 * px转dp
	 *
	 * @param context
	 * @param pxVal
	 * @return
	 */
	public static int px2Dp(Context context, float pxVal) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxVal / scale + 0.5f);
	}

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 *
	 * @param spValue
	 * @param context （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	private static LayoutInflater inflater;

	public static View inflate(Context context, int res) {
		if (inflater == null) {
			inflater = LayoutInflater.from(context);
		}
		return inflater.inflate(res, null);
	}

	/**
	 * 获取屏幕宽
	 *
	 * @param context
	 * @return
	 */
	public static int getSreenWidth(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		return wm.getDefaultDisplay().getWidth();
	}

	/**
	 * 获取屏幕高
	 *
	 * @param context
	 * @return
	 */
	public static int getSreenHeight(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		return wm.getDefaultDisplay().getHeight();
	}
}
