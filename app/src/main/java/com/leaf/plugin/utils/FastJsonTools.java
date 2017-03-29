package com.leaf.plugin.utils;


import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Data:2016-05-10 17:50
 * Created by YJG
 */
public class FastJsonTools {

    private static String TAG = "FastJsonTools";

    /**
     * 用fastjson 将json字符串解析为一个 JavaBean
     *
     * @param jsonString
     * @param cls
     * @return
     */
    public static <T> T getObject(String jsonString, Class<T> cls) {
        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }
        T t = null;
        try {
            t = JSON.parseObject(jsonString, cls);
        } catch (Exception e) {
            Log.e(TAG, "is not json format");
        }
        return t;
    }


    /**
     * 用fastjson 将json字符串 解析成为一个 List<JavaBean> 及 List<String>
     *
     * @param jsonString
     * @param cls
     * @return
     */
    public static <T> List<T> getObjectArray(String jsonString, Class<T> cls) {
        List<T> list = new ArrayList<T>();
        if (TextUtils.isEmpty(jsonString)) {
            return list;
        }
        try {
            list = JSON.parseArray(jsonString, cls);
        } catch (Exception e) {
            Log.e(TAG, "is not json format");
        }
        return list;
    }


    /**
     * 用fastjson 将jsonString 解析成 List<Map<String,Object>>
     *
     * @param jsonString
     * @return
     */
    public static List<Map<String, Object>> getListMap(String jsonString) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            // 两种写法
            // list = JSON.parseObject(jsonString, new
            // TypeReference<List<Map<String, Object>>>(){}.getType());

            list = JSON.parseObject(jsonString, new TypeReference<List<Map<String, Object>>>() {
            });
        } catch (Exception e) {
            Log.e(TAG, "is not json format");
        }
        return list;

    }


    /**
     * fastjson  将json字符串 通过key解析成为一个 Object
     *
     * @param jsonString
     * @param key
     * @return
     */
    public static Object key2Object(String jsonString, String key) {
        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }
        Object mObj = null;
        try {
            Map<String, Object> ArrayMap = jsonToMap(jsonString);
            if (ArrayMap != null && ArrayMap.size() > 0) {
                if (ArrayMap.containsKey(key)) {
                    mObj = ArrayMap.get(key);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "is not json format");
        }
        return mObj;
    }

    /**
     * 通过key ,解析成List
     *
     * @param jsonString
     * @param key
     * @param Listcls
     * @param <T>
     * @return 返回 list<T>
     */
    public static <T> List<T> key2Array(String jsonString, String key, Class<T> Listcls) {
        List<T> list = new ArrayList<T>();
        Object mObj = key2Object(jsonString, key);
        if (mObj != null) {
            return getObjectArray(mObj.toString(), Listcls);
        }
        return list;
    }

    /**
     * 通过key ,解析成Bean
     *
     * @param jsonString
     * @param key
     * @param cls
     * @param <T>
     * @return 返回 Bean
     */
    public static <T> T key2Bean(String jsonString, String key, Class<T> cls) {
        Object mObj = key2Object(jsonString, key);
        if (mObj != null) {
            return getObject(mObj.toString(), cls);
        }
        return null;
    }

    /**
     * fastjson  将json字符串 解析成为一个  Map<String, Object>
     *
     * @param jsonString
     * @return
     */
    @Nullable
    public static Map<String, Object> jsonToMap(String jsonString) {
        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }
        return JSON.parseObject(jsonString, new TypeReference<Map<String, Object>>() {
        });
    }


}
