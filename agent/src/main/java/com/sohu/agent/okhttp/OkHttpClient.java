package com.sohu.agent.okhttp;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Dns;
//import okhttp3.EventListener;
import okhttp3.Interceptor;

public class OkHttpClient {

    private Dns dns;

    private final List<Interceptor> interceptors = new ArrayList<>();

    private final List<Interceptor> networkInterceptors = new ArrayList<>();

    // private EventListener.Factory eventListenerFactory;

    public OkHttpClient() {
        initOkhttp();
    }

    private void initOkhttp() {
        this.dns = OkHttpHooker.getInstance().getOkhttpDns();
        this.interceptors.addAll(OkHttpHooker.getInstance().getGlobalInterceptors());
        this.networkInterceptors.addAll(OkHttpHooker.getInstance().getGlobalNetworkInterceptors());
    }
}