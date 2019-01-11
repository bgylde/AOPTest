package com.transform.demo.okhttp;

import com.sohu.agent.okhttp.OkHttpHooker;
import com.sohu.agent.utils.LogUtils;
import com.transform.demo.utils.StringUtils;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by wangyan on 2019/1/10
 */
public class OkHttpHelper {

    private OkHttpClient mOkHttpClient;

    private final static String TAG = "OkHttpHelper";

    private static final int DEFAULT_CONN_TIMEOUT = 5;      // 连接超时时间
    private static final int DEFAULT_READ_TIMEOUT = 5;      // 读取超时时间
    private static final int DEFAULT_WRITE_TIMEOUT = 10;    // 写入超时时间

    private static OkHttpHelper instance = null;

    private OkHttpHelper() {
//        OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder();
//        httpBuilder.connectTimeout(DEFAULT_CONN_TIMEOUT, TimeUnit.SECONDS)
//                .readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.SECONDS)
//                .writeTimeout(DEFAULT_WRITE_TIMEOUT, TimeUnit.SECONDS);
//
//        List<Interceptor> globalInterceptors = OkHttpHooker.getInstance().getGlobalInterceptors();
//        for (Interceptor interceptor : globalInterceptors) {
//            httpBuilder.addInterceptor(interceptor);
//        }
//
//        mOkHttpClient = httpBuilder.build();

        OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder();
        httpBuilder.connectTimeout(DEFAULT_CONN_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_WRITE_TIMEOUT, TimeUnit.SECONDS);

        mOkHttpClient = httpBuilder.build();
    }

    public static OkHttpHelper getInstance() {
        if (instance == null) {
            synchronized (OkHttpHelper.class) {
                if (instance == null) {
                    instance = new OkHttpHelper();
                }
            }
        }

        return instance;
    }

    public static void destoryInstance() {
        if (instance == null) {
            return;
        }

        instance.mOkHttpClient = null;
        instance = null;
    }

    public void enqueue(Request request, Map<String, String> params, Callback callback) {
        FormBody.Builder builder = null;
        if (params != null) {
            builder = new FormBody.Builder();
            for (String key : params.keySet()) {
                builder.add(key,params.get(key));
            }
        }

        try {
            mOkHttpClient.newCall(request).enqueue(callback);
        } catch (Exception e) {
            LogUtils.e(TAG, e);
        }
    }
    /**
     * 同步请求
     * @param request
     * @return 返回请求的String
     */
    public String execute(Request request) {
        Response response = null;
        String resString = null;
        ResponseBody body = null;
        try {
            response = realExecute(request);
            if (response == null) {
                LogUtils.w(TAG, "execute error response is null!");
            } else {
                body = response.body();
                if (body != null) {
                    resString = body.string();
                    LogUtils.i(TAG, "response string: " + resString);
                }
            }
        } catch (Exception e) {
            LogUtils.e(TAG, e);
        } finally {
            if (body != null) {
                body.close();
            }
        }

        return resString;
    }

    private Response realExecute(Request request) {
        Response response = null;
        try {
            response = mOkHttpClient.newCall(request).execute();
            LogUtils.i(TAG, "execute require response: " + response);
        } catch (Exception e) {
            LogUtils.e(TAG, e);
        }
        return response;
    }

    public Object execute(Request request, Map<String, String> params, IResultParser parser) throws IOException {
        FormBody.Builder builder = null;
        if (params != null) {
            builder = new FormBody.Builder();
            for (String key : params.keySet()) {
                builder.add(key,params.get(key));
            }
        }

        Object object = null;
        Response response = null;
        try {
            response = mOkHttpClient.newCall(request).execute();
            if (response != null) {
                OkHttpSession session = buildSessionData(request, response);
                object = parser.parse(response, session.getJson());
            }
        } catch (IOException e) {
            LogUtils.e(TAG, e);
        } catch (Exception e) {
            LogUtils.e(TAG, e);
        } finally {
            if (response != null) {
                ResponseBody body = response.body();
                if (body != null) {
                    body.close();
                }
            }
        }

        return object;
    }

    private OkHttpSession buildSessionData(Request request, Response response) {
        OkHttpSession session = new OkHttpSession();
        session.setRequest(request);
        session.setResponse(response);

        setSessionInfo(session, response);

        return session;
    }

    private void setSessionInfo(OkHttpSession session, Response response) {

        if (response == null || response.body() == null) {
            return;
        }

        try {
            String json = response.body().string();
            session.setJson(json);

            if (StringUtils.isNotBlank(json)) {

                JSONObject jsonObject = new JSONObject(json);
                if (jsonObject.has("status")) {
                    session.setCode(jsonObject.getInt("status"));
                }

                if (jsonObject.has("statusText")) {
                    session.setMsg(jsonObject.getString("statusText"));
                }

                if (jsonObject.has("errorCode")) {
                    session.setErrorCode(jsonObject.getInt("errorCode"));
                }

            }
        } catch (Exception e) {
            LogUtils.e(TAG, e);
        }
    }
}
