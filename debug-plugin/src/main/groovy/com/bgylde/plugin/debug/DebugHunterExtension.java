package com.bgylde.plugin.debug;

import com.bgylde.transform.RunVariant;

/**
 * Created by wangyan on 2019/1/12
 */
public class DebugHunterExtension {

    public RunVariant runVariant = RunVariant.ALWAYS;
    public boolean duplcatedClassSafeMode = false;

    @Override
    public String toString() {
        return "DebugHunterExtension{" +
                "runVariant=" + runVariant +
                ", duplcatedClassSafeMode=" + duplcatedClassSafeMode +
                '}';
    }
}
