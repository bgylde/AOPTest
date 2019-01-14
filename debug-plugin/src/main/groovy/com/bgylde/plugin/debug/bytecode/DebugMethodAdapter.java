package com.bgylde.plugin.debug.bytecode;

import com.bgylde.plugin.debug.bytecode.utils.Parameter;
import com.bgylde.plugin.debug.bytecode.utils.Utils;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;

import java.util.List;

/**
 * Created by wangyan on 2019/1/12
 */
public class DebugMethodAdapter extends LocalVariablesSorter implements Opcodes {

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
    }

    @Override
    public void visitCode() {
        super.visitCode();
        System.out.println("----------- START INJECT DEBUG INFO TO " + className + methodName + " -----------");
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

            if (returnType != Type.VOID_TYPE || opcode == ATHROW) {
                int loadOpcode = Utils.getLoadOpcodeFromType(returnType);
                if (opcode == ATHROW) {
                    loadOpcode = ALOAD;
                }

                mv.visitVarInsn(loadOpcode, resultTempValIndex);
                mv.visitMethodInsn(INVOKESTATIC, "com/bgylde/agent/utils/LogUtils", "d", "(Ljava/lang/String;)V", false);
                mv.visitVarInsn(loadOpcode, resultTempValIndex);
            } else {
                mv.visitLdcInsn("VOID");
                mv.visitMethodInsn(INVOKESTATIC, "com/bgylde/agent/utils/LogUtils", "d", "(Ljava/lang/String;)V", false);
            }
        }

        super.visitInsn(opcode);
    }
}
