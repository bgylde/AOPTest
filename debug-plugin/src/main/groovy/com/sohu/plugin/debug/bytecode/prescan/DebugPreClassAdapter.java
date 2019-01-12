package com.sohu.plugin.debug.bytecode.prescan;

import com.sohu.plugin.debug.bytecode.utils.Parameter;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangyan on 2019/1/12
 */
public class DebugPreClassAdapter extends ClassVisitor {

    private Map<String, List<Parameter>> methodParametersMap = new HashMap<>();
    private DebugPreMethodAdapter methodAdapter;
    private boolean needParameter = false;

    private boolean classEnable = false;

    public DebugPreClassAdapter(final ClassVisitor cv) {
        super(Opcodes.ASM5, cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        if ("com/transform/demo/debug/DebugTest".equals(name)) {
            classEnable = true;
        }
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        if (classEnable) {
            needParameter = true;
            String methodKey = name + desc;
            methodAdapter = new DebugPreMethodAdapter(methodKey, methodParametersMap, mv);
            return methodAdapter;
        } else {
            return mv;
        }
    }

    public Map<String, List<Parameter>> getMethodParametersMap() {
        return methodParametersMap;
    }

    public boolean isNeedParameter() {
        return needParameter;
    }
}
