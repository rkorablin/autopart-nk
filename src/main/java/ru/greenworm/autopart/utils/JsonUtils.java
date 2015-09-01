package ru.greenworm.autopart.utils;

import org.json.JSONObject;

public final class JsonUtils {

	private JsonUtils() {

	}

	public static String getSuccessJson() {
		return "{\"result\":\"success\"}";
	}
	
	public static String getErrorJson(String message) {
		JSONObject json = new JSONObject();
		json.put("result", "error");
		json.put("message", message);
		return json.toString();
	}

}
