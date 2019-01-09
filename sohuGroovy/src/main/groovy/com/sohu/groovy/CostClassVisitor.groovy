package com.sohu.groovy

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.commons.AdviceAdapter

import org.objectweb.asm.Opcodes

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
                    // todo MethodEnter

                }
                super.onMethodEnter()
            }

            @Override
            protected void onMethodExit(int opcode) {
                if (inject) {
                    // todo MethodExit

                }
                super.onMethodExit(opcode)
            }
        }

        return mv
    }
}