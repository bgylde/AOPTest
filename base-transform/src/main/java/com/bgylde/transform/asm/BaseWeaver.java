package com.bgylde.transform.asm;

import org.apache.commons.io.FileUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.attribute.FileTime;
import java.util.Enumeration;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Created by quinn on 07/09/2018
 * asm相关 参考 https://www.jianshu.com/p/abd1b1b8d3f3
 */
public abstract class BaseWeaver implements IWeaver{

    private static final FileTime ZERO = FileTime.fromMillis(0);
    protected ClassLoader classLoader;

    public BaseWeaver() {
    }

    public final void weaveJar(File inputJar, File outputJar) throws IOException {
        ZipFile inputZip = new ZipFile(inputJar);
        ZipOutputStream outputZip = new ZipOutputStream(new BufferedOutputStream(
                java.nio.file.Files.newOutputStream(outputJar.toPath())));
        Enumeration<? extends ZipEntry> inEntries = inputZip.entries();
        while (inEntries.hasMoreElements()) {
            ZipEntry entry = inEntries.nextElement();
            InputStream originalFile =
                    new BufferedInputStream(inputZip.getInputStream(entry));
            ZipEntry outEntry = new ZipEntry(entry.getName());
            byte[] newEntryContent;
            if (!isWeavableClass(outEntry.getName().replace("/", "."))) {
                newEntryContent = org.apache.commons.io.IOUtils.toByteArray(originalFile);
            } else {
                newEntryContent = weaveSingleClassToByteArray(originalFile);
            }
            CRC32 crc32 = new CRC32();
            crc32.update(newEntryContent);
            outEntry.setCrc(crc32.getValue());
            outEntry.setMethod(ZipEntry.STORED);
            outEntry.setSize(newEntryContent.length);
            outEntry.setCompressedSize(newEntryContent.length);
            outEntry.setLastAccessTime(ZERO);
            outEntry.setLastModifiedTime(ZERO);
            outEntry.setCreationTime(ZERO);
            outputZip.putNextEntry(outEntry);
            outputZip.write(newEntryContent);
            outputZip.closeEntry();
        }
        outputZip.flush();
        outputZip.close();
    }

    public final void weaveSingleClassToFile(File inputFile, File outputFile, String inputBaseDir) throws IOException {
        if(!inputBaseDir.endsWith("/")) inputBaseDir = inputBaseDir + "/";
        if(isWeavableClass(inputFile.getAbsolutePath().replace(inputBaseDir, "").replace("/", "."))) {
            FileUtils.touch(outputFile);
            InputStream inputStream = new FileInputStream(inputFile);
            byte[] bytes = weaveSingleClassToByteArray(inputStream);
            FileOutputStream fos = new FileOutputStream(outputFile);
            fos.write(bytes);
            fos.close();
            inputStream.close();
        } else {
            if (inputFile.isFile()) {
                FileUtils.touch(outputFile);
                FileUtils.copyFile(inputFile, outputFile);
            }
        }
    }

    /**
     * 设置类加载器
     * @param classLoader 类加载器
     */
    public final void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * 遍历过程，实现注入，注入过程在accept中
     * @param inputStream 需要注入的class的字节码
     * @return 注入以后的结果字节码
     * @throws IOException
     * ClassReader.SKIP_DEBUG: 扫描中略过行号相关内容
     */
    @Override
    public byte[] weaveSingleClassToByteArray(InputStream inputStream) throws IOException {
        ClassReader classReader = new ClassReader(inputStream);
        ClassWriter classWriter = new ExtendClassWriter(classLoader, ClassWriter.COMPUTE_MAXS);
        ClassVisitor classWriterWrapper = wrapClassWriter(classWriter);
        classReader.accept(classWriterWrapper, ClassReader.EXPAND_FRAMES);
        return classWriter.toByteArray();
    }

    // 设置配置类
    public abstract void setExtension(Object extension);

    protected abstract ClassVisitor wrapClassWriter(ClassWriter classWriter);

    /**
     * 过滤基础的类名，比如R.class、BuildConfig.class、R&...等类，这些类直接略过不进行代码注入
     * @param fullQualifiedClassName 类名
     * @return 是否进行代码注入 true-注入 false-不注入
     */
    @Override
    public boolean isWeavableClass(String fullQualifiedClassName){
        return fullQualifiedClassName.endsWith(".class") && !fullQualifiedClassName.contains("R$") && !fullQualifiedClassName.contains("R.class") && !fullQualifiedClassName.contains("BuildConfig.class");
    }
}
