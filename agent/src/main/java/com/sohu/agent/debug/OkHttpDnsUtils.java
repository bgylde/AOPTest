package com.sohu.agent.debug;

import com.sohu.agent.utils.LogUtils;

import java.net.InetAddress;
import java.util.List;

/**
 * Created by wangyan on 2019/1/12
 * 作为处理okhttp的dns处理使用
 */
public class OkHttpDnsUtils {

    private static final String TAG = "OkHttpDnsUtils";

    public static void handleDnsList(List<InetAddress> addresses, long useTime) {
        LogUtils.d(TAG, "dns parse use time: " + useTime + " ms");
        if (addresses != null && addresses.size() > 0) {
            for (InetAddress address : addresses) {
                LogUtils.d(TAG, "hostname: " + address.getHostName() + " address: " + address.getHostAddress());
            }
        }
    }

//    @Override
//    public List<InetAddress> lookup(String hostname) throws UnknownHostException {
//        long startTime = System.currentTimeMillis();
//
//        if (hostname == null) throw new UnknownHostException("hostname == null");
//        List list =  Arrays.asList(InetAddress.getAllByName(hostname));
//
//        long useTime = System.currentTimeMillis() - startTime;
//        handleDnsList(list, useTime);
//
//        return list;
//    }
}
