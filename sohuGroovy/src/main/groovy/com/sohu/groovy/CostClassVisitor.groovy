package com.sohu.groovy

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.commons.AdviceAdapter
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import com.sohu.groovytest.Cost

public class CostClassVisitor extends ClassVisitor {

    public CostClassVisitor(final int flag) {
        this(flag, null);
    }
    public CostClassVisitor(final int flag, ClassVisitor classVisitor) {
        super(flag, classVisitor)
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        mv = new AdviceAdapter(Opcodes.ASM5, mv, access, name, desc) {
            private boolean inject = false;     // 根据是否有注解判断是否注入

            @Override
            AnnotationVisitor visitAnnotation(String descs, boolean visible) {
                // 过滤注解Cost.class
                if (Type.getDescriptor(Cost.class).equals(descs)) {
                    inject = true;
                }

                return super.visitAnnotation(descs, visible)
            }

            @Override
            protected void onMethodEnter() {
                if (inject) {
                    mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                    mv.visitLdcInsn("========start=========");
                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

                    mv.visitLdcInsn(name);
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false);
                    mv.visitMethodInsn(INVOKESTATIC, "com/sohu/agent/TimeCache", "setStartTime", "(Ljava/lang/String;J)V", false);
                }
                super.onMethodEnter()
            }

            @Override
            protected void onMethodExit(int opcode) {
                if (inject) {
                    mv.visitLdcInsn(name);
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false);
                    mv.visitMethodInsn(INVOKESTATIC, "com/sohu/agent/TimeCache", "setEndTime", "(Ljava/lang/String;J)V", false);

                    mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                    mv.visitLdcInsn(name);
                    mv.visitMethodInsn(INVOKESTATIC, "com/sohu/agent/TimeCache", "getCostTime", "(Ljava/lang/String;)Ljava/lang/String;", false);
                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

                    mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                    mv.visitLdcInsn("========end=========");
                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
                }
                super.onMethodExit(opcode)
            }
        }

        return mv
    }
}