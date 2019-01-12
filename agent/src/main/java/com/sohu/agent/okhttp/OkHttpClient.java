package com.sohu.agent.okhttp;

import com.sohu.agent.timing.TimeCache;
import com.sohu.agent.utils.LogUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
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
        initDns();
    }

    private void initOkhttp() {
        this.dns = OkHttpHooker.getInstance().getOkhttpDns();
        this.interceptors.addAll(OkHttpHooker.getInstance().getGlobalInterceptors());
        this.networkInterceptors.addAll(OkHttpHooker.getInstance().getGlobalNetworkInterceptors());
    }

    private void initDns() {
        Dns dns = new Dns() {
            @Override
            public List<InetAddress> lookup(String hostname) throws UnknownHostException {
                TimeCache.setStartTime("lookup", System.nanoTime());

                if (hostname == null) throw new UnknownHostException("hostname == null");
                InetAddress[] addresses = InetAddress.getAllByName(hostname);

                TimeCache.setEndTime("lookup", System.nanoTime());

                return Arrays.asList(addresses);
            }
        };

        this.dns = dns;
    }

    private String setValue(String value, int intValue) {
        LogUtils.d(value);

        value = value + intValue;

        return value;
    }
}