package org.geekpower.utils;

import static com.google.gson.reflect.TypeToken.getParameterized;

import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonUtil {

	// 不用创建对象,直接使用Gson.就可以调用方法
	private static Gson gson = null;
	// 判断gson对象是否存在了,不存在则创建对象
	static {
		if (gson == null) {
			// gson = new Gson();
			// 当使用GsonBuilder方式时属性为空的时候输出来的json字符串是有键值key的,显示形式是"key":null，而直接new出来的就没有"key":null的
			gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		}
	}

	// 无参的私有构造方法
	private GsonUtil() {
	}

	/**
	 * 将对象转成json格式
	 * 
	 * @param object
	 * @return String
	 */
	public static String toJson(Object object) {
		return gson.toJson(object);
	}

	/**
	 * 将json转成特定的cls的对象
	 * 
	 * @param json
	 * @param cls
	 * @return
	 */
	public static <T> T fromJson(String json, Class<T> cls) {
		return gson.fromJson(json, cls);
	}

	/**
	 * json字符串转成泛型list
	 * 
	 * @param json
	 * @param cls
	 * @return
	 */
	public static <T> List<T> jsonToList(String json, Class<T> cls) {
		return gson.fromJson(json, getParameterized(List.class, cls).getType());
	}

	/**
	 * json字符串转成map的
	 * 
	 * @param json
	 * @return
	 */
	public static <T> Map<String, T> jsonToMaps(String json, Class<T> valCls) {
		return gson.fromJson(json, getParameterized(Map.class, String.class, valCls).getType());
	}

}