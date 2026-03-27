package com.martin.appserver.utils;

import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

public class JsonUtils {

    public static String JSON(Object data) {
        try {
            if (data instanceof JSONObject || data instanceof JSONArray) {
                return data.toString();
            }
            return data instanceof Map ? (new JSONObject((Map) data)).toString() : JSONObject.wrap(data).toString();
        } catch (Exception e) {
            return "{\"status\":\"error\",\"message\":\"JSON parse error\"}";
        }
    }

    public static JSONObject obj() {
        return new JSONObject();
    }

    public static JSONArray arr() {
        return new JSONArray();
    }

    public static String responseOk(Object data) {
        try {
            JSONObject res = new JSONObject();
            res.put("status", "ok");
            res.put("data", data);
            return res.toString();
        } catch (Exception e) {
            return responseError("JSON build error");
        }
    }

    public static String responseError(String message) {
        try {
            JSONObject res = new JSONObject();
            res.put("status", "error");
            res.put("message", message);
            return res.toString();
        } catch (Exception e) {
            return "{\"status\":\"error\",\"message\":\"fatal json error\"}";
        }
    }
}
