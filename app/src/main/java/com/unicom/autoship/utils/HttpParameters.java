package com.unicom.autoship.utils;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
/**
 * Created by dyzhou on 2018/8/10.
 */
public class HttpParameters {
	private long idx;
	private HashMap<String, Object> mMap = new HashMap<String, Object>();
	private LinkedHashMap<String, String> mLinkMap = new LinkedHashMap<>();

	public HttpParameters() {
	}

	public Bundle getBundleParams() {
		Bundle b = new Bundle();
		for (Iterator<String> ite = mMap.keySet().iterator(); ite.hasNext();) {
			String key = ite.next();
			Object value = mMap.get(key);
			if (value instanceof String) {
				b.putString(key, (String) mMap.get(key));
			} else if (value instanceof Integer) {
				b.putInt(key, (Integer) mMap.get(key));
			} else if (value instanceof Double) {
				b.putDouble(key, (Double) mMap.get(key));
			} else if (value instanceof Long) {
				b.putLong(key, (Long) mMap.get(key));
			} else if (value instanceof Boolean) {
				b.putBoolean(key, (Boolean) mMap.get(key));
			} else if (value instanceof Float) {
				b.putFloat(key, (Float) mMap.get(key));
			}
		}
		return b;
	}

	public String getJsonStr() {
		if (mMap != null && mMap.size() > 0) {
			return JsonUtil.jsonFromObject(mMap);
		}
		return "";
	}

	public String getUrlStrWithParams (String uri) {

		Uri.Builder builder = Uri.parse(uri)
				.buildUpon();
		for (Iterator<String> item = mLinkMap.keySet().iterator(); item.hasNext();) {
			String key = item.next();
			builder.appendQueryParameter(key, mLinkMap.get(key));
		}
		return builder.build().toString();
	}

	public void setIdx(long idx) {
		this.idx = idx;
	}

	public long getIdx() {
		return idx;
	}

	public HashMap<String, Object> getMap() {
		return this.mMap;
	}

	public void add(String key, Object value) {
		if (!TextUtils.isEmpty(key) && value != null) {
			mMap.put(key, value);
			mLinkMap.put(key, String.valueOf(value));
		}
	}

	public void remove(String key) {
		if (this.mMap.containsKey(key))
			this.mMap.remove(key);
	}

	public Object getValue(String key) {
		return this.mMap.get(key);
	}

	public int size() {
		return mMap.size();
	}

}
