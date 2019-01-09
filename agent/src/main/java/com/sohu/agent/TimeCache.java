package com.sohu.agent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangyan on 2019/1/9
 */
public class TimeCache {

    private static Map<String, Long> startTime = new HashMap<>();
    private static Map<String, Long> endTime = new HashMap<>();

    public static void setStartTime(String methodName, long time) {
        startTime.put(methodName, time);
    }

    public static void setEndTime(String methodName, long time) {
        endTime.put(methodName, time);
    }

    public static String getCostTime(String methodName) {
        long start = startTime.get(methodName);
        long end = endTime.get(methodName);

        return methodName + " cost time: " + String.valueOf(end - start) + " ms";
    }

    public static void print(String str) {
        System.out.println(str);
    }
}
