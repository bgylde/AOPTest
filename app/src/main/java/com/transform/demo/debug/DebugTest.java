package com.transform.demo.debug;

import com.bgylde.agent.debug.Debug;
import com.bgylde.agent.utils.LogUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by wangyan on 2019/1/12
 */
public class DebugTest {

    @Debug
    public static String setValue(String value, long longValue, int type, boolean enable) {

        StringBuilder sb = new StringBuilder();
        sb.append(value).append(longValue).append(type).append(enable);
        return sb.toString();
    }

    @Debug
    public static void testVoid(int xy) {
        int y = 6 * xy - 3;
        int result = xy / (xy - y);

        int res = result * 4;
        LogUtils.d(String.valueOf(res));
    }

    //@Debug
    public static List<InetAddress> lookup(String hostname) throws UnknownHostException {
        if (hostname == null) throw new UnknownHostException("hostname == null");
        return Arrays.asList(InetAddress.getAllByName(hostname));
    }
}
