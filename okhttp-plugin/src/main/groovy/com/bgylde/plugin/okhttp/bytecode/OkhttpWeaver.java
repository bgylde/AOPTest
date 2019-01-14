package com.bgylde.plugin.okhttp.bytecode;

import com.bgylde.transform.asm.BaseWeaver;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

/**
 * Created by wangyan on 2019/1/11
 */
public class OkhttpWeaver extends BaseWeaver {

    private com.bgylde.plugin.okhttp.OkhttpHunterExtension extension;

    @Override
    public void setExtension(Object extension) {
        if (extension == null) {
            return;
        }

        this.extension = (com.bgylde.plugin.okhttp.OkhttpHunterExtension) extension;
    }

    @Override
    protected ClassVisitor wrapClassWriter(ClassWriter classWriter) {
        return new OkhttpClassAdapter(classWriter, this.extension.weaveEventListener);
    }
}
