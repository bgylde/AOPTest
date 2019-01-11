package com.sohu.agent.timing;

import com.sohu.agent.utils.LogUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangyan on 2019/1/9
 */
public class TimeCache {

    private static Map<String, Long> timeCache = new HashMap<>();

    public static void setStartTime(String methodName, long time) {
        timeCache.put(methodName, time);
    }

    public static void setEndTime(String methodName, long time) {
        Long result = timeCache.get(methodName);
        if (result != null) {
            timeCache.remove(methodName);
            long startTime = result;
            LogUtils.d("CostTime", methodName + " cost time: " + String.valueOf(time - startTime) + " ns");
        }
    }
}
