package com.bgylde.plugin.debug.bytecode.prescan;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wangyan on 2019/1/12
 */
public class DebugPreMethodAdapter extends MethodVisitor implements Opcodes {

    private boolean debugEnable = false;

    private Label startLabel;

    private String methodKey;

    private List<com.bgylde.plugin.debug.bytecode.utils.Parameter> parameters = new ArrayList<>();

    private Map<String, List<com.bgylde.plugin.debug.bytecode.utils.Parameter>> methodParametersMap;

    public DebugPreMethodAdapter(String methodKey, Map<String, List<com.bgylde.plugin.debug.bytecode.utils.Parameter>> methodParametersMap, MethodVisitor mv) {
        super(Opcodes.ASM5, mv);
        this.methodKey = methodKey;
        this.methodParametersMap = methodParametersMap;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        if (desc.equals("Lcom/bgylde/agent/debug/Debug;")) {
            debugEnable = true;
        }

        return super.visitAnnotation(desc, visible);
    }

    @Override
    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
        if (debugEnable && !"this".equals(name) && startLabel != null && startLabel == start) {
            Type type = Type.getType(desc);
            if (type.getSort() == Type.OBJECT || type.getSort() == Type.ARRAY) {
                parameters.add(new com.bgylde.plugin.debug.bytecode.utils.Parameter(name, "Ljava/lang/Object;", index));
            } else {
                parameters.add(new com.bgylde.plugin.debug.bytecode.utils.Parameter(name, desc, index));
            }
        }
        super.visitLocalVariable(name, desc, signature, start, end, index);
    }

    @Override
    public void visitEnd() {
        if (parameters != null && parameters.size() > 0) {
            methodParametersMap.put(methodKey, parameters);
        }

        super.visitEnd();
    }

    @Override
    public void visitLabel(Label label) {
        if (startLabel == null) {
            startLabel = label;
        }

        super.visitLabel(label);
    }
}
