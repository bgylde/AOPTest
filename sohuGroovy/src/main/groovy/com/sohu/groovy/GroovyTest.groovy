package com.sohu.groovy;

import org.gradle.api.Plugin;

import com.android.build.api.transform.*;
import com.android.build.gradle.AppExtension;
import com.android.build.gradle.internal.pipeline.TransformManager;
import org.apache.commons.codec.digest.DigestUtils;
import com.android.utils.FileUtils;

import org.gradle.api.Project;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.ClassVisitor;

import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class GroovyTest extends Transform implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        AppExtension appExtension = (AppExtension)project.getProperties().get("android");
        appExtension.registerTransform(this, Collections.EMPTY_LIST);

        //def android = project.extensions.getByType(AppExtension);
        //android.registerTransform(this);
    }

    @Override
    public String getName() {
        return "groovyTest";
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    @Override
    public boolean isIncremental() {
        return true;         // 开启增量编译
    }

    @Override
    public void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {
        System.out.println("-------------------------- asm visit start --------------------------");

        // 如果非增量，则清空旧的输出内容
        if (!isIncremental) {
            outputProvider.deleteAll();
        }

        long startTime = System.currentTimeMillis();
        //inputs.each { TransformInput input ->
        for (TransformInput input : inputs) {
            //input.directoryInputs.each { DirectoryInput directoryInput ->
            for (DirectoryInput directoryInput : input.getDirectoryInputs()) {
                // 遍历class并被ASM操作
                if (directoryInput.getFile().isDirectory()) {
                    directoryInput.file.eachFileRecurse { File file ->
                    //for (File file : directoryInput.getFile().eachFileRecurse {}) {
                        String name = file.getName();
                        if (name.endsWith(".class") && !name.startsWith("R\$") && !"R.class".equals(name) && !"BuildConfig.class".equals(name)) {
                            System.out.println(name + " is changing...");
                            InputStream inputStream = new FileInputStream(file);
                            ClassReader cr = new ClassReader(inputStream);
                            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
                            ClassVisitor cv = new CostClassVisitor(Opcodes.ASM5, cw);
                            cr.accept(cv, ClassReader.EXPAND_FRAMES);
                            byte[] code = cw.toByteArray();
                            //String filePath = file.parentFile.absoluteFile + File.separator + name
                            FileOutputStream fos = new FileOutputStream(String.valueOf(file.getParentFile().getAbsoluteFile()) + File.separator + name);
                            fos.write(code);
                            fos.close();
                            System.out.println(name + " has been changed.");
                        }
                    }
                }

                File dest = outputProvider.getContentLocation(directoryInput.getName(), directoryInput.getContentTypes(), directoryInput.getScopes(), Format.DIRECTORY);
                FileUtils.copyDirectory(directoryInput.getFile(), dest);
            }

            //input.jarInputs.each { JarInput jarInput ->
            for (JarInput jarInput : input.getJarInputs()) {
                String jarName = jarInput.getName();
                String md5Name = DigestUtils.md5Hex(jarInput.getFile().getAbsolutePath());
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length() - 4);
                }

                File dest = outputProvider.getContentLocation(jarName + md5Name, jarInput.getContentTypes(), jarInput.getScopes(), Format.JAR);
                FileUtils.copyFile(jarInput.getFile(), dest);
            }
        }

        long cost = (System.currentTimeMillis() - startTime);
        System.out.println("plugin cost " + cost + " ms");
        System.out.println("-------------------------- asm visit end --------------------------");
    }
}