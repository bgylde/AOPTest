package com.sohu.plugin.debug.bytecode.utils;

/**
 * Created by wangyan on 2019/1/12
 */
public class Parameter {

    private final String name;

    private final String desc;

    private final int index;

    public Parameter(String name, String desc, int index) {
        this.name = name;
        this.desc = desc;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public String getDesc() {
        return desc;
    }

    public String getName() {
        return name;
    }
}
