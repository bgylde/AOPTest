package com.bgylde.plugin.debug.bytecode;

import com.bgylde.plugin.debug.bytecode.utils.Parameter;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.List;
import java.util.Map;

/**
 * Created by wangyan on 2019/1/12
 */
public class DebugClassAdapter extends ClassVisitor {

    private String className;

    private Map<String, List<com.bgylde.plugin.debug.bytecode.utils.Parameter>> methodMap;

    DebugClassAdapter(final ClassVisitor cv, final Map<String, List<com.bgylde.plugin.debug.bytecode.utils.Parameter>> methodMap) {
        super(Opcodes.ASM5, cv);
        this.methodMap = methodMap;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.className = name;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        List<Parameter> parameters = methodMap.get(name + desc);
        if (parameters != null && parameters.size() > 0) {
            DebugMethodAdapter methodAdapter = new DebugMethodAdapter(className, parameters, name, access, desc, mv);
            return mv == null ? null : methodAdapter;
        } else {
            return mv;
        }
    }
}
