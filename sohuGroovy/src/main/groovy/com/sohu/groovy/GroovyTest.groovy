package com.sohu.groovy

import org.gradle.api.Plugin

import com.android.build.api.transform.*
import com.android.build.gradle.AppExtension
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.codec.digest.DigestUtils
import com.android.utils.FileUtils

import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.ClassVisitor

import org.objectweb.asm.Opcodes

import com.sohu.groovy.CostClassVisitor

public class GroovyTest extends Transform implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def android = project.extensions.getByType(AppExtension)
        android.registerTransform(this)
    }

    @Override
    String getName() {
        return "groovyTest"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {
        println('-------------------------- asm visit start --------------------------')

        def startTime = System.currentTimeMillis()
        inputs.each { TransformInput input ->
            input.directoryInputs.each { DirectoryInput directoryInput ->
                // 遍历class并被ASM操作
                if (directoryInput.file.isDirectory()) {
                    directoryInput.file.eachFileRecurse { File file ->
                        def name = file.name
                        if (name.endsWith(".class") && !name.startsWith("R\$") && !"R.class".equals(name) && !"BuildConfig.class".equals(name)) {
                            println name + ' is changing...'
                            println('0. #######################')
                            ClassReader cr = new ClassReader(file.bytes)
                            println('1. #######################')
                            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS)
                            println('2. #######################')
                            ClassVisitor cv = new CostClassVisitor(Opcodes.ASM5, cw)
                            println('3. #######################')
                            cr.accept(cv, ClassReader.EXPAND_FRAMES);
                            println('4. #######################')
                            byte[] code = cw.toByteArray();
                            println('5. #######################')
                            //String filePath = file.parentFile.absoluteFile + File.separator + name
                            StringBuilder sb = new StringBuilder();
                            sb.append(file.parentFile.absoluteFile).append(File.separator).append(name);
                            FileOutputStream fos = new FileOutputStream(sb.toString());
                            println('6. #######################')
                            fos.write(code);
                            println('7. #######################')
                            fos.close();
                            println name + ' has been changed.'
                        }
                    }
                }

                println('8. #######################')
                def dest = outputProvider.getContentLocation(directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)
                println('9. #######################')
                FileUtils.copyDirectory(directoryInput.file, dest)
                println('10. #######################')
            }

            input.jarInputs.each { JarInput jarInput ->
                def jarName = jarInput.name
                def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length() - 4)
                }

                def dest = outputProvider.getContentLocation(jarName + md5Name, jarInput.contentTypes, jarInput.scopes, Format.JAR)
                FileUtils.copyFile(jarInput.file, dest)
            }
        }

        def cost = (System.currentTimeMillis() - startTime) / 1000
        println("plugin cost $cost secs")
        println('-------------------------- asm visit end --------------------------')
    }
}