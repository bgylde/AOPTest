package com.sohu.plugin.okhttp;

import com.sohu.transform.RunVariant;

/**
 * Created by wangyan on
 */
public class OkhttpHunterExtension {

    public RunVariant runVariant = RunVariant.ALWAYS;
    public boolean weaveEventListener = true;
    public boolean duplcatedClassSafeMode = false;

    @Override
    public String toString() {
        return "OkHttpHunterExtension{" + "runVariant=" + runVariant +
                ", weaveEventListener=" + weaveEventListener +
                ", duplcatedClassSafeMode=" + duplcatedClassSafeMode + "}";
    }
}