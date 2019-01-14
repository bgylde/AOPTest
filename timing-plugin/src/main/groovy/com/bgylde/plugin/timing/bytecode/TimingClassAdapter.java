package com.bgylde.plugin.timing.bytecode;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.File;

// 参考博文 https://my.oschina.net/ta8210/blog/163637
public final class TimingClassAdapter extends ClassVisitor{

    private String className;

    private boolean isHeritedFromBlockHandler = false;

    TimingClassAdapter(final ClassVisitor cv) {
        super(Opcodes.ASM5, cv);
    }

    /**
     * ClassVisitor首先调用
     * @param version 类的版本，如1.1---1.7等
     * @param access 类的修饰符，如public、private、protected、final、extends、接口、抽象类、注解类型、枚举类型、标记@Deprecated等
     * @param name 类名称
     * @param signature 泛型信息如果没有则为null，基于接口会有所不同，包含两个: e.g. <T extends Date, V extends ArrayList> 就是 <T:Ljava/util/Date;V:Ljava/util/ArrayList;>Ljava/lang/Object;
     * @param superName 继承的父类
     * @param interfaces 类实现的接口 e.g. [java/io/Serializable, java/util/List]
     */
    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        //this.isHeritedFromBlockHandler = Arrays.toString(interfaces).contains("com/hunter/library/timing/IBlockHandler");
        this.isHeritedFromBlockHandler = false;
        this.className = name;
    }

    /**
     * 扫描到类注解时声明
     * @param desc 注解类型 e.g. @Bean({ "" })即为：Lnet/hasor/core/gift/bean/Bean;
     * @param visible 表示注解在JVM中是否可见 true：虚拟机可见
     * @return
     */
    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        return super.visitAnnotation(desc, visible);
    }

    /**
     * 扫描到类中字段时调用
     * @param access 修饰符，即 ACC_PUBLIC（public）、ACC_PRIVATE（private）、ACC_PROTECTED（protected）、ACC_STATIC（static）、ACC_FINAL（final）、ACC_VOLATILE（volatile）、ACC_TRANSIENT（transient）、ACC_ENUM（枚举）、ACC_DEPRECATED（标记了@Deprecated注解的字段）、ACC_SYNTHETIC。
     * @param name 表示字段名称
     * @param desc 表示字段类型：（"L" + 类型路径 + ";"） e.g. Ljava/lang/Object;
     * @param signature 表示泛型信息： ("T" + 泛型名 + ";")
     * @param value 默认值，只能用来表示基本数据类型 byte short int long float double boolean String, 字段为finla类型且有值默认值才有值
     * @return
     */
    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        return super.visitField(access, name, desc, signature, value);
    }

    /**
     * 扫描到类的方法时进行调用
     * @param access 方法的修饰符
     * @param name 方法名
     * @param desc 方法签名 Class<?>, String, Object... paramType 为 Ljava/lang/Class;Ljava/lang/String;[Ljava/lang/Object;
     * @param signature 泛型
     * @param exceptions 抛出的异常 e.g. throws Throwable, Exception => [java/lang/Throwable, java/lang/Exception]
     * @return
     */
    @Override
    public MethodVisitor visitMethod(final int access, final String name,
                                     final String desc, final String signature, final String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        if(isHeritedFromBlockHandler) {
            return mv;
        } else {
            return mv == null ? null : new TimingMethodAdapter(className + File.separator + name, access, desc, mv);
        }
    }

    /**
     * 完成类扫描时调用，可以追加某些方法
     */
    @Override
    public void visitEnd() {
        super.visitEnd();
    }
}