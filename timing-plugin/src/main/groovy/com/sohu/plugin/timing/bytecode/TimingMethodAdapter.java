package com.sohu.plugin.timing.bytecode;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;

public final class TimingMethodAdapter extends LocalVariablesSorter implements Opcodes {

    private boolean inject = false;

    private String methodName;

    public TimingMethodAdapter(String name, int access, String desc, MethodVisitor mv) {
        super(Opcodes.ASM5, access, desc, mv);
        this.methodName = name.replace("/", ".");
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        if (desc.equals("Lcom/sohu/agent/Cost;")) {
            inject = true;
        }

        return super.visitAnnotation(desc, visible);
    }

    @Override
    public void visitCode() {
        super.visitCode();
        if (inject) {
            System.out.println("----------------------- START CHANGE METHOD " + methodName + " -----------------------");
            mv.visitLdcInsn(methodName);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false);
            mv.visitMethodInsn(INVOKESTATIC, "com/sohu/agent/TimeCache", "setStartTime", "(Ljava/lang/String;J)V", false);
        }
    }

    @Override
    public void visitInsn(int opcode) {
        // if ((opcode >= IRETURN && opcode <= RETURN) || opcode == ATHROW) {
        if (inject) {
            mv.visitLdcInsn(methodName);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false);
            mv.visitMethodInsn(INVOKESTATIC, "com/sohu/agent/TimeCache", "setEndTime", "(Ljava/lang/String;J)V", false);
            System.out.println("----------------------- END CHANGE METHOD " + methodName + " -----------------------");
            inject = false;
            // mv.visitLdcInsn(methodName);
            // mv.visitMethodInsn(INVOKESTATIC, "com/sohu/agent/TimeCache", "getCostTime", "(Ljava/lang/String;)V", false);
        }
        super.visitInsn(opcode);
    }
}
