package com.sohu.plugin.debug.bytecode;

import com.android.build.gradle.internal.LoggerWrapper;
import com.sohu.plugin.debug.DebugHunterExtension;
import com.sohu.plugin.debug.bytecode.prescan.DebugPreClassAdapter;
import com.sohu.transform.asm.BaseWeaver;
import com.sohu.transform.asm.ExtendClassWriter;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by wangyan on 2019/1/12
 */
public class DebugWeaver extends BaseWeaver {

    private static final String PLUGIN_LIBRARY = "com.sohu.agent.debug";
    private static final LoggerWrapper logger = LoggerWrapper.getLogger(DebugWeaver.class);

    @Override
    public void setExtension(Object extension) {}

    @Override
    protected ClassVisitor wrapClassWriter(ClassWriter classWriter) {
        return classWriter;
    }

    @Override
    public byte[] weaveSingleClassToByteArray(InputStream inputStream) throws IOException {
        ClassReader classReader = new ClassReader(inputStream);
        ClassWriter classWriter = new ExtendClassWriter(classLoader, ClassWriter.COMPUTE_MAXS);
        DebugPreClassAdapter debugPreClassAdapter = new DebugPreClassAdapter(classWriter);
        classReader.accept(debugPreClassAdapter, ClassReader.EXPAND_FRAMES);

        if (debugPreClassAdapter.isNeedParameter()) {
            classWriter = new ExtendClassWriter(classLoader, ClassWriter.COMPUTE_MAXS);
            DebugClassAdapter debugClassAdapter = new DebugClassAdapter(classWriter, debugPreClassAdapter.getMethodParametersMap());
            classReader.accept(debugClassAdapter, ClassReader.EXPAND_FRAMES);
        }

        return classWriter.toByteArray();
    }

    @Override
    public boolean isWeavableClass(String fullQualifiedClassName) {
        return super.isWeavableClass(fullQualifiedClassName) && !fullQualifiedClassName.startsWith(PLUGIN_LIBRARY);
    }
}
