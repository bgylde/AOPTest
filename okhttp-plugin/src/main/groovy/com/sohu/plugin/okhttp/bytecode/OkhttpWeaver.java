package com.sohu.plugin.okhttp.bytecode;

import com.sohu.plugin.okhttp.OkhttpHunterExtension;
import com.sohu.transform.asm.BaseWeaver;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

/**
 * Created by wangyan on 2019/1/11
 */
public class OkhttpWeaver extends BaseWeaver {

    private OkhttpHunterExtension extension;

    @Override
    public void setExtension(Object extension) {
        if (extension == null) {
            return;
        }

        this.extension = (OkhttpHunterExtension) extension;
    }

    @Override
    protected ClassVisitor wrapClassWriter(ClassWriter classWriter) {
        return new OkhttpClassAdapter(classWriter, this.extension.weaveEventListener);
    }
}
