package com.transform.demo.utils;

/**
 * Created by wangyan on 2019/1/10
 */
public class StringUtils {

    public static boolean isNotBlank(String str) {
        return str != null && str.trim().length() > 0;
    }
}
