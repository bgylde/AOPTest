package com.sohu.agent.okhttp;

import com.sohu.agent.utils.LogUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Connection;
import okhttp3.Dns;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by wangyan on 2019/1/10
 */
public class OkHttpHooker {

    private static final String TAG = "OkHttpHooker";

    private static OkHttpHooker instance = null;

    private Dns okhttpDns = null;
    private List<Interceptor> globalInterceptors = null;
    private List<Interceptor> globalNetworkInterceptors = null;

    private OkHttpHooker() {
        initIntercepters();
    }

    public static OkHttpHooker getInstance() {
        if (instance == null) {
            synchronized (OkHttpHooker.class) {
                if (instance == null) {
                    instance = new OkHttpHooker();
                }
            }
        }

        return instance;
    }

    public Dns getOkhttpDns() {
        if (okhttpDns == null) {
            synchronized (this) {
                if (okhttpDns == null) {
                    okhttpDns = Dns.SYSTEM;
                }
            }
        }

        return okhttpDns;
    }

    public List<Interceptor> getGlobalInterceptors() {
        if (globalInterceptors == null) {
            synchronized (this) {
                globalInterceptors = new ArrayList<>();
            }
        }

        return globalInterceptors;
    }

    public void addGlobalInterceptor(Interceptor interceptor) {
        if (globalInterceptors == null) {
            synchronized (this) {
                globalInterceptors = new ArrayList<>();
            }
        }

        globalInterceptors.add(interceptor);
    }

    public List<Interceptor> getGlobalNetworkInterceptors() {
        if (globalNetworkInterceptors == null) {
            synchronized (this) {
                if (globalNetworkInterceptors == null) {
                    globalNetworkInterceptors = new ArrayList<>();
                }
            }
        }

        return globalNetworkInterceptors;
    }

    public void addGlobalNetworkInterceptor(Interceptor interceptor) {
        if (globalNetworkInterceptors == null) {
            synchronized (this) {
                if (globalNetworkInterceptors == null) {
                    globalNetworkInterceptors = new ArrayList<>();
                }
            }
        }

        globalNetworkInterceptors.add(interceptor);
    }

    private void initIntercepters() {
        Interceptor globalInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                LogUtils.d(TAG, "Request url: " + request.url());
                LogUtils.d(TAG, "Request method: " + request.method());
                Headers requestHeards = request.headers();
                for (int i = 0; i < requestHeards.size(); i++) {
                    LogUtils.d(TAG, "Request header name:[" + requestHeards.name(i) + "] value:[" + requestHeards.value(i) + "]");
                }

                RequestBody body = request.body();
                if (body != null) {
                    LogUtils.d(TAG, "Request body length: " + body.contentLength());
                }
                Connection connection = chain.connection();
                if (connection != null) {
                    LogUtils.d(TAG, "protocol: " + connection.protocol());
                    LogUtils.d(TAG, "route: " + connection.route().toString());
                    LogUtils.d(TAG, "address: " + connection.route().address());
                    LogUtils.d(TAG, "proxy: " + connection.route().proxy());
                }

                long start = System.currentTimeMillis();
                Response response = chain.proceed(request);
                LogUtils.d(TAG, "Net request end. Cost time: " + (System.currentTimeMillis() - start) + " ms");

                Headers headers = response.headers();
                for (int i = 0; i < headers.size(); i++) {
                    LogUtils.d(TAG, "Response header name:[" + headers.name(i) + "] value:[" + headers.value(i) + "]");
                }

                LogUtils.d(TAG, "Response url: " + response.request().url());
                LogUtils.d(TAG, "Response code: " + response.code());
                LogUtils.d(TAG, "Response message: " + response.message());
                LogUtils.d(TAG, "protocal: " + response.protocol());
                ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    LogUtils.d(TAG, "Response body length: " + responseBody.contentLength());
                }

                Response networkResponse = response.networkResponse();
                if (networkResponse != null) {
                    LogUtils.d(TAG, "networkResponse: " + networkResponse.toString());
                    Headers netResheaders = networkResponse.headers();
                    for (int i = 0; i < netResheaders.size(); i++) {
                        LogUtils.d(TAG, "Response header name:[" + netResheaders.name(i) + "] value:[" + netResheaders.value(i) + "]");
                    }
                }

                return response;
            }
        };

        addGlobalInterceptor(globalInterceptor);
    }
}
