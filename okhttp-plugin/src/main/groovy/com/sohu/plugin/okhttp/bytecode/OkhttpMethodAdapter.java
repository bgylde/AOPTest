package com.sohu.plugin.okhttp.bytecode;

import com.android.build.gradle.internal.LoggerWrapper;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.LocalVariablesSorter;

/**
 * Created by wangyan on 2019/1/11
 */
public class OkhttpMethodAdapter extends LocalVariablesSorter implements Opcodes {

    private static final LoggerWrapper logger = LoggerWrapper.getLogger(OkhttpMethodAdapter.class);

    private boolean defaultOkhttpClientBuilderInitMethod = false;

    private boolean weaveEventListener;

    OkhttpMethodAdapter(String name, int access, String desc, MethodVisitor mv, boolean weaveEventListener) {
        super(Opcodes.ASM5, access, desc, mv);
        if ("okhttp3/OkHttpClient$Builder/<init>".equals(name) && "()V".equals(desc)) {
            defaultOkhttpClientBuilderInitMethod = true;
        }

        this.weaveEventListener = weaveEventListener;
    }

    @Override
    public void visitInsn(int opcode) {
        if (defaultOkhttpClientBuilderInitMethod && ((opcode >= IRETURN && opcode <= RETURN) || opcode == ATHROW)) {
            logger.info("##### Inject to okhttp start #####");
            if (weaveEventListener) {
                //todo 添加okhttp的工厂类EventListenerFactory, 由于OKHTTP在3.11.0以上才支持EventListenerFactory，这里先不加
            }

            // DNS
            logger.info("##### Add DNS to okhttp #####");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESTATIC, "com/sohu/agent/okhttp/OkHttpHooker", "getInstance", "()Lcom/sohu/agent/okhttp/OkHttpHooker;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/sohu/agent/okhttp/OkHttpHooker", "getOkhttpDns", "()Lokhttp3/Dns;", false);
            mv.visitFieldInsn(PUTFIELD, "okhttp3/OkHttpClient$Builder", "dns", "Lokhttp3/Dns;");

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
        }

        super.visitInsn(opcode);
    }
}
