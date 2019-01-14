package com.transform.demo.okhttp;

import android.support.annotation.NonNull;

import com.bgylde.agent.utils.LogUtils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by wangyan on 2019/1/10
 */
public class OkHttpTest {

    private static final String TAG = "OkHttpTest";

    private static final String TEST_URL = "https://kyfw.12306.cn/otn/leftTicket/queryZ?leftTicketDTO.train_date=2019-02-01&leftTicketDTO.from_station=BXP&leftTicketDTO.to_station=TNV&purpose_codes=ADULT";

    public static void okHttpTest() {

        Request request = new Request.Builder()
                .url(TEST_URL)
                .build();

        OkHttpHelper.getInstance().enqueue(request, null, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                LogUtils.e(TAG, e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ResponseBody body = response.body();
                if (body != null) {
                    LogUtils.d(TAG, "response: " + body.string());
                    body.close();
                }
            }
        });
    }
}
