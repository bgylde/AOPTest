package com.sohu.plugin.okhttp.bytecode;

import com.android.build.gradle.internal.LoggerWrapper;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;

/**
 * Created by wangyan on 2019/1/11
 */
public class OkhttpMethodAdapter extends LocalVariablesSorter implements Opcodes {

    private static final LoggerWrapper logger = LoggerWrapper.getLogger(OkhttpMethodAdapter.class);

    private boolean defaultOkhttpClientBuilderInitMethod = false;

    private boolean okhttpDnsEnable = false;

    private boolean weaveEventListener;

    private String classname;

    // 记录dns的lookup函数开始的时间
    private int dnsStartTimeIndex = -1;

    private String methodDesc;

    OkhttpMethodAdapter(String name, int access, String desc, MethodVisitor mv, boolean weaveEventListener) {
        super(Opcodes.ASM5, access, desc, mv);
        if ("okhttp3/OkHttpClient$Builder/<init>".equals(name) && "()V".equals(desc)) {
            defaultOkhttpClientBuilderInitMethod = true;
        } else if (name.endsWith("lookup")) {
            okhttpDnsEnable = true;
        }

        this.classname = name;
        this.methodDesc = desc;
        this.weaveEventListener = weaveEventListener;
    }

    @Override
    public void visitCode() {
        super.visitCode();
        dnsStartTimeIndex = newLocal(Type.LONG_TYPE);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
        mv.visitVarInsn(LSTORE, dnsStartTimeIndex);
    }

    @Override
    public void visitInsn(int opcode) {
        if (((opcode >= IRETURN && opcode <= RETURN) || opcode == ATHROW)) {
            if (defaultOkhttpClientBuilderInitMethod) {
                logger.info("##### Inject to okhttp start #####");
                if (weaveEventListener) {
                    //todo 添加okhttp的工厂类EventListenerFactory, 由于OKHTTP在3.11.0以上才支持EventListenerFactory，这里先不加
                }

                // DNS 此处不再添加dns，防止对应用添加的dns覆盖，在之后对DNS的构造函数做字节插入做统计
                // logger.info("##### Add DNS to okhttp #####");
                // mv.visitVarInsn(ALOAD, 0);
                // mv.visitMethodInsn(INVOKESTATIC, "com/sohu/agent/okhttp/OkHttpHooker", "getInstance", "()Lcom/sohu/agent/okhttp/OkHttpHooker;", false);
                // mv.visitMethodInsn(INVOKEVIRTUAL, "com/sohu/agent/okhttp/OkHttpHooker", "getOkhttpDns", "()Lokhttp3/Dns;", false);
                // mv.visitFieldInsn(PUTFIELD, "okhttp3/OkHttpClient$Builder", "dns", "Lokhttp3/Dns;");

                // Interceptor
                logger.info("##### Add Interceptor to okhttp #####");
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, "okhttp3/OkHttpClient$Builder", "interceptors", "Ljava/util/List;");
                mv.visitMethodInsn(INVOKESTATIC, "com/sohu/agent/okhttp/OkHttpHooker", "getInstance", "()Lcom/sohu/agent/okhttp/OkHttpHooker;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "com/sohu/agent/okhttp/OkHttpHooker", "getGlobalInterceptors", "()Ljava/util/List;", false);
                mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "addAll", "(Ljava/util/Collection;)Z", true);
                mv.visitInsn(POP);

                // NetworkInterceptor
                logger.info("##### Add NetworkInterceptor to okhttp #####");
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, "okhttp3/OkHttpClient$Builder", "networkInterceptors", "Ljava/util/List;");
                mv.visitMethodInsn(INVOKESTATIC, "com/sohu/agent/okhttp/OkHttpHooker", "getInstance", "()Lcom/sohu/agent/okhttp/OkHttpHooker;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "com/sohu/agent/okhttp/OkHttpHooker", "getGlobalNetworkInterceptors", "()Ljava/util/List;", false);
                mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "addAll", "(Ljava/util/Collection;)Z", true);
                mv.visitInsn(POP);

                logger.info("##### Inject okhttp end #####");
            } else if (okhttpDnsEnable && ATHROW != opcode) {   // 对DNS 的构造函数做插入，统计时间等信息
                logger.info("##### Inject to okhttp DNS start ##### " + classname);
                Type returnType = Type.getReturnType(methodDesc);
                if (Type.VOID_TYPE != returnType) {
                    int resultTempValIndex = newLocal(returnType);
                    int storeOpcode = Utils.getStoreOpcodeFromType(returnType);
                    mv.visitVarInsn(storeOpcode, resultTempValIndex);
                    int loadOpcode = Utils.getLoadOpcodeFromType(returnType);
                    mv.visitVarInsn(loadOpcode, resultTempValIndex);
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
                    mv.visitVarInsn(LLOAD, dnsStartTimeIndex);
                    mv.visitInsn(LSUB);
                    mv.visitMethodInsn(INVOKESTATIC, "com/sohu/agent/debug/OkHttpDnsUtils", "handleDnsList", "(Ljava/util/List;J)V", false);
                    mv.visitVarInsn(loadOpcode, resultTempValIndex);
                }

                logger.info("##### Inject to okhttp DNS end ##### " + classname);
            }
        }

        super.visitInsn(opcode);
    }
}
