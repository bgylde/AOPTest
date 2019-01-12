package com.sohu.plugin.debug.bytecode;

import com.sohu.plugin.debug.bytecode.utils.Parameter;
import com.sohu.plugin.debug.bytecode.utils.Utils;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;

import java.util.List;

/**
 * Created by wangyan on 2019/1/12
 */
public class DebugMethodAdapter extends LocalVariablesSorter implements Opcodes {

    // 过滤是否是okhttp的dns的lookup方法，暂时okhttp的dns监控放在这边
    private boolean dnsEnable = false;

    // 记录dns的lookup函数开始的时间
    private int dnsStartTimeIndex = -1;

    private List<Parameter> parameters;

    private String className;

    private String methodName;

    private String methodDesc;

    DebugMethodAdapter(String className, List<Parameter> parameters, String name, int access, String desc, MethodVisitor mv) {
        super(Opcodes.ASM5, access, desc, mv);
        if (!className.endsWith("/")) {
            this.className = className.substring(className.lastIndexOf("/") + 1);
        } else {
            this.className = className;
        }

        this.parameters = parameters;
        this.methodName = name;
        this.methodDesc = desc;
        if ("lookup".equals(methodName)) {
            dnsEnable = true;
        }
    }

    @Override
    public void visitCode() {
        super.visitCode();
        System.out.println("----------- START INJECT DEBUG INFO TO " + className + methodName + " -----------");
        dnsStartTimeIndex = newLocal(Type.LONG_TYPE);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
        mv.visitVarInsn(LSTORE, dnsStartTimeIndex);
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
        System.out.println("----------- END INJECT DEBUG INFO TO " + className + methodName + " -----------");
    }

    @Override
    public void visitInsn(int opcode) {
        if (((opcode >= IRETURN && opcode <= RETURN) || opcode == ATHROW)) {
            Type returnType = Type.getReturnType(methodDesc);
            int resultTempValIndex = -1;
            if (Type.VOID_TYPE != returnType || ATHROW == opcode) {
                resultTempValIndex = newLocal(returnType);
                int storeOpcode = Utils.getStoreOpcodeFromType(returnType);
                if (opcode == ATHROW)
                    storeOpcode = ASTORE;
                mv.visitVarInsn(storeOpcode, resultTempValIndex);
            }

            if (!dnsEnable) {
                if (returnType != Type.VOID_TYPE || opcode == ATHROW) {
                    int loadOpcode = Utils.getLoadOpcodeFromType(returnType);
                    if (opcode == ATHROW) {
                        loadOpcode = ALOAD;
                    }

                    mv.visitVarInsn(loadOpcode, resultTempValIndex);
                    mv.visitMethodInsn(INVOKESTATIC, "com/sohu/agent/utils/LogUtils", "d", "(Ljava/lang/String;)V", false);
                    mv.visitVarInsn(loadOpcode, resultTempValIndex);
                } else {
                    mv.visitLdcInsn("VOID");
                    mv.visitMethodInsn(INVOKESTATIC, "com/sohu/agent/utils/LogUtils", "d", "(Ljava/lang/String;)V", false);
                }
            } else { // 对DNS中的lookup插入字节码
                if (returnType != Type.VOID_TYPE || opcode == ATHROW) {
                    int loadOpcode = Utils.getLoadOpcodeFromType(returnType);
                    if (opcode == ATHROW) {
                        loadOpcode = ALOAD;
                    }

                    mv.visitVarInsn(loadOpcode, resultTempValIndex);
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
                    mv.visitVarInsn(LLOAD, dnsStartTimeIndex);
                    mv.visitInsn(LSUB);
                    mv.visitMethodInsn(INVOKESTATIC, "com/sohu/agent/debug/OkHttpDnsUtils", "handleDnsList", "(Ljava/util/List;J)V", false);
                    mv.visitVarInsn(loadOpcode, resultTempValIndex);
                }
            }
        }

        super.visitInsn(opcode);
    }
}
