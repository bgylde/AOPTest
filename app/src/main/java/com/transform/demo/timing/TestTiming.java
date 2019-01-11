package com.transform.demo.timing;

import com.sohu.agent.timing.Cost;
import com.sohu.agent.utils.LogUtils;

/**
 * Created by wangyan on 2019/1/10
 */
public class TestTiming {

    private static final String TAG = "TestTiming";

    public static void sleepTimingTest(long time) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                sleepHandle(time);
            }
        });
        thread.start();
    }

    @Cost
    private static void sleepHandle(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            LogUtils.e(TAG, e);
        }
    }
}
