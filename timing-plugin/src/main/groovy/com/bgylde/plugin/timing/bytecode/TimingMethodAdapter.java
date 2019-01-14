package com.bgylde.plugin.timing.bytecode;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.LocalVariablesSorter;

/**
 * 参考博文 https://my.oschina.net/ta8210/blog/220011
 * 档ClassReader读取到Method时转入{@link MethodVisitor}接口处理
 * {@link LocalVariablesSorter}是{@link MethodVisitor}的子类，也可以直接继承{@link AdviceAdapter}类实现MethodVisitor
 *
 * 静态代码块会放在一个"<clinit>"方法中； 构造函数是"<init>"方法
 */
public final class TimingMethodAdapter extends LocalVariablesSorter implements Opcodes {

    // 过滤是否注入代码，可以根据注解、类名、方法名等各种条件
    private boolean inject = false;

    private String methodName;

    public TimingMethodAdapter(String name, int access, String desc, MethodVisitor mv) {
        super(Opcodes.ASM5, access, desc, mv);
        this.methodName = name.replace("/", ".");
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        // 根据注解过滤，只对有Cost注解的方法注入代码
        // Type.getDescriptor(Cost.class).equals(descs)也可以过滤，但是这样会需要应用Cost类，比较麻烦，此处直接比较字符串
        if (desc.equals("Lcom/bgylde/agent/timing/Cost;")) {
            inject = true;
        }

        return super.visitAnnotation(desc, visible);
    }

    /**
     * 开始扫描这个方法
     */
    @Override
    public void visitCode() {
        super.visitCode();
        if (inject) {
            System.out.println("----------------------- START CHANGE METHOD " + methodName + " -----------------------");
            mv.visitLdcInsn(methodName);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false);
            mv.visitMethodInsn(INVOKESTATIC, "com/bgylde/agent/timing/TimeCache", "setStartTime", "(Ljava/lang/String;J)V", false);
        }
    }

    /**
     * visitEnd之前调用，可以反复调用，用来确定类方法在执行时候的堆栈大小，与visitCode成对出现
     * @param maxStack
     * @param maxLocals
     */
    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        super.visitMaxs(maxStack, maxLocals);
    }

    /**
     * 表示方法输出完毕
     */
    @Override
    public void visitEnd() {
        super.visitEnd();
    }

    @Override
    public void visitInsn(int opcode) {
        // ATHROW 为抛出异常，也表示方法执行完毕
        if (inject && ((opcode >= IRETURN && opcode <= RETURN) || opcode == ATHROW)) {
            mv.visitLdcInsn(methodName);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false);
            mv.visitMethodInsn(INVOKESTATIC, "com/bgylde/agent/timing/TimeCache", "setEndTime", "(Ljava/lang/String;J)V", false);
            System.out.println("----------------------- END CHANGE METHOD " + methodName + " -----------------------");
            inject = false;     // 经过opcode过滤可以保证这个方法只被调用一次，此处不加也可以~
        }

        super.visitInsn(opcode);
    }
}
