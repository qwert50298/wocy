package com.unicom.autoship.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
/**
 * Created by dyzhou on 2018/8/10.
 */
public class JsonUtil {
	private static final Gson gson = new Gson();
	private static final JsonParser parser = new JsonParser();

	public static String jsonFromObject(Object object) {
		return gson.toJson(object);
	}
	
	/**
	 * 将字符串转换为Json
	 * @param json
	 * @return
	 */
	public static JsonObject jsonFromString(String json){
		return parser.parse(json).getAsJsonObject();
	}

	public static <T> T objectFromJson(String json, Class<T> klass) {
		if (TextUtils.isEmpty(json)) {
			return null;
		}
		return gson.fromJson(json, klass);
	}
	public static <T> T objectFromJson(JsonObject json, Class<T> klass) {
		if (json==null || json.isJsonNull()) {
			return null;
		}
		return gson.fromJson(json, klass);
	}

	/**
	 * 博联的 SDK 设备类 iOS 和 android 封装的不一样
	 * 导致 传到服务器再取先来的时候互不兼容
	 * 在这里做了转换
	 * iOS ->　android
	 * newConfig -> newconfig (0/1 -> true/false)
	 * controlKey -> key
	 * controlId -> id
	 */
	public static <T> T objectFromJsonWithBoo (String json, Class<T> tClass) {
		if (TextUtils.isEmpty(json)) {
			return null;
		}
		JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
		if (jsonObject.has("newConfig")) {
			jsonObject.addProperty("newconfig", jsonObject.get("newConfig").getAsInt() == 1 ? "true" : "fasle");
			jsonObject.addProperty("lock", jsonObject.get("lock").getAsInt() == 1 ? "true" : "false");
			jsonObject.addProperty("key", jsonObject.get("controlKey").getAsString());
			jsonObject.addProperty("id", jsonObject.get("controlId").getAsInt());
			return objectFromJson(jsonObject, tClass);
		}
		return objectFromJson(json, tClass);
	}
}
