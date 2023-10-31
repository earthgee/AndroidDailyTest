package com.earthgee.systrace;

import com.earthgee.systrace.compat.AgpCompat;
import com.earthgee.systrace.javautil.FileUtil;
import com.earthgee.systrace.javautil.Log;
import com.earthgee.systrace.javautil.Util;
import com.earthgee.systrace.retrace.MappingCollector;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Created by zhaoruixuan1 on 2023/10/31
 * CopyRight (c) haodf.com
 * 功能：hotfix nuwa
 */
public class HotfixDirector {

    private static final String TAG = "HotfixDirector";

    private final ExecutorService executor;
    private MappingCollector mappingCollector;

    private volatile boolean traceError = false;

    public HotfixDirector(ExecutorService executor, MappingCollector mappingCollector) {
        this.executor = executor;
        this.mappingCollector = mappingCollector;
    }

    public void director(Map<File, File> srcFolderList, Map<File, File> dependencyJarList, ClassLoader classLoader)
            throws ExecutionException, InterruptedException {
        List<Future> futures = new LinkedList<>();
        directorMethodFromSrc(srcFolderList, futures, classLoader);
        directorMethodFromJar(dependencyJarList, futures, classLoader);

        for (Future future : futures) {
            future.get();
        }
        if (traceError) {
            throw new IllegalArgumentException("something wrong with trace, see detail log before");
        }
        futures.clear();
    }

    private void directorMethodFromSrc(Map<File, File> srcMap, List<Future> futures, final ClassLoader classLoader) {
        if (null != srcMap) {
            for (Map.Entry<File, File> entry : srcMap.entrySet()) {
                futures.add(executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        innerDirectorMethodFromSrc(entry.getKey(), entry.getValue(), classLoader);
                    }
                }));
            }
        }
    }

    private void directorMethodFromJar(Map<File, File> dependencyMap, List<Future> futures, final ClassLoader classLoader) {
        if (null != dependencyMap) {
            for (Map.Entry<File, File> entry : dependencyMap.entrySet()) {
                futures.add(executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        innerDirectorMethodFromJar(entry.getKey(), entry.getValue(), classLoader);
                    }
                }));
            }
        }
    }

    private void innerDirectorMethodFromSrc(File input, File output, ClassLoader classLoader) {
        ArrayList<File> classFileList = new ArrayList<>();
        if (input.isDirectory()) {
            listClassFiles(classFileList, input);
        } else {
            classFileList.add(input);
        }


        for (File classFile : classFileList) {
            InputStream is = null;
            FileOutputStream os = null;
            try {
                final String changedFileInputFullPath = classFile.getAbsolutePath();
                final File changedFileOutput = new File(changedFileInputFullPath.replace(input.getAbsolutePath(), output.getAbsolutePath()));

                if (changedFileOutput.getCanonicalPath().equals(classFile.getCanonicalPath())) {
                    //输入输出一样时报错
                    throw new RuntimeException("Input file(" + classFile.getCanonicalPath() + ") should not be same with output!");
                }

                if (!changedFileOutput.exists()) {
                    changedFileOutput.getParentFile().mkdirs();
                }
                changedFileOutput.createNewFile();

                if (MethodCollector.isNeedTraceFile(classFile.getName())) {
                    try {
                        is = new FileInputStream(classFile);
                        ClassReader classReader = new ClassReader(is);
                        ClassWriter classWriter = new TraceClassWriter(ClassWriter.COMPUTE_FRAMES, classLoader);
                        ClassVisitor classVisitor = new DirectorClassAdapter(AgpCompat.getAsmApi(), classWriter);
                        classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);
                        is.close();

                        byte[] data = classWriter.toByteArray();

                        if (output.isDirectory()) {
                            os = new FileOutputStream(changedFileOutput);
                        } else {
                            os = new FileOutputStream(output);
                        }
                        os.write(data);
                        os.close();
                    } catch (IndexOutOfBoundsException exception) {
                        Log.e(TAG, "inject src error classFile:%s e:%s", input.getAbsolutePath(),
                                classFile.getAbsolutePath(), exception.getMessage());
                        FileUtil.copyFileUsingStream(classFile, changedFileOutput);
                    }
                } else {
                    FileUtil.copyFileUsingStream(classFile, changedFileOutput);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "[innerTraceMethodFromSrc] input path:%s error classFile:%s input e:%s", input.getAbsolutePath(),
                        classFile.getAbsolutePath(), e.getMessage());
                try {
                    Files.copy(input.toPath(), output.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            } finally {
                try {
                    is.close();
                    os.close();
                } catch (Exception e) {
                    // ignore
                }
            }
        }
    }

    private void innerDirectorMethodFromJar(File input, File output, final ClassLoader classLoader) {
        ZipOutputStream zipOutputStream = null;
        ZipFile zipFile = null;
        try {
            zipOutputStream = new ZipOutputStream(new FileOutputStream(output));
            zipFile = new ZipFile(input);
            Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
            while (enumeration.hasMoreElements()) {
                ZipEntry zipEntry = enumeration.nextElement();
                String zipEntryName = zipEntry.getName();

                if (Util.preventZipSlip(output, zipEntryName)) {
                    Log.e(TAG, "Unzip entry %s failed!", zipEntryName);
                    continue;
                }

                if (MethodCollector.isNeedTraceFile(zipEntryName)) {
                    InputStream inputStream = zipFile.getInputStream(zipEntry);
                    ClassReader classReader = new ClassReader(inputStream);
                    ClassWriter classWriter = new TraceClassWriter(ClassWriter.COMPUTE_FRAMES, classLoader);
                    ClassVisitor classVisitor = new DirectorClassAdapter(AgpCompat.getAsmApi(), classWriter);
                    classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);
                    byte[] data = classWriter.toByteArray();

                    InputStream byteArrayInputStream = new ByteArrayInputStream(data);
                    ZipEntry newZipEntry = new ZipEntry(zipEntryName);
                    FileUtil.addZipEntry(zipOutputStream, newZipEntry, byteArrayInputStream);
                } else {
                    InputStream inputStream = zipFile.getInputStream(zipEntry);
                    ZipEntry newZipEntry = new ZipEntry(zipEntryName);
                    FileUtil.addZipEntry(zipOutputStream, newZipEntry, inputStream);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "[innerTraceMethodFromJar] input:%s output:%s e:%s", input, output, e.getMessage());
            if (e instanceof ZipException) {
                e.printStackTrace();
            }
            try {
                if (input.length() > 0) {
                    Files.copy(input.toPath(), output.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } else {
                    Log.e(TAG, "[innerTraceMethodFromJar] input:%s is empty", input);
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } finally {
            try {
                if (zipOutputStream != null) {
                    zipOutputStream.finish();
                    zipOutputStream.flush();
                    zipOutputStream.close();
                }
                if (zipFile != null) {
                    zipFile.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "close stream err!");
            }
        }
    }

    private void listClassFiles(ArrayList<File> classFiles, File folder) {
        File[] files = folder.listFiles();
        if (null == files) {
            Log.e(TAG, "[listClassFiles] files is null! %s", folder.getAbsolutePath());
            return;
        }
        for (File file : files) {
            if (file == null) {
                continue;
            }
            if (file.isDirectory()) {
                listClassFiles(classFiles, file);
            } else {
                if (null != file && file.isFile()) {
                    classFiles.add(file);
                }

            }
        }
    }

    private class DirectorClassAdapter extends ClassVisitor {

        private int version;
        private String className;
        private String superName;
        private boolean isHookClass;

        public DirectorClassAdapter(int api, ClassVisitor classVisitor) {
            super(api, classVisitor);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces);
            this.version = version;
            this.className = name;
            this.superName = superName;
            this.isHookClass = isHookClass(className);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            if (isHookClass) {
                MethodVisitor methodVisitor = cv.visitMethod(access, name, descriptor, signature, exceptions);
                MethodVisitor resultMethodVisitor = new DirectorMethodAdapter(api, methodVisitor, access, name, descriptor);
                if(this.version <= Opcodes.V1_6) {
                    return new JSRAdapter(api, resultMethodVisitor, access, name, descriptor, signature, exceptions);
                }
                return resultMethodVisitor;
            } else {
                return super.visitMethod(access, name, descriptor, signature, exceptions);
            }
        }
    }

    private class DirectorMethodAdapter extends AdviceAdapter {

        private final String methodName;
        private boolean isConstructor;

        /**
         * Constructs a new {@link AdviceAdapter}.
         *
         * @param api           the ASM API version implemented by this visitor. Must be one of {@link
         *                      Opcodes#ASM4}, {@link Opcodes#ASM5}, {@link Opcodes#ASM6} or {@link Opcodes#ASM7}.
         * @param methodVisitor the method visitor to which this adapter delegates calls.
         * @param access        the method's access flags (see {@link Opcodes}).
         * @param name          the method's name.
         * @param descriptor    the method's descriptor.
         */
        protected DirectorMethodAdapter(int api, MethodVisitor methodVisitor, int access, String name, String descriptor) {
            super(api, methodVisitor, access, name, descriptor);
            this.methodName = name;

            if("<init>".equals(name)) {
                isConstructor = true;
            }
        }

        @Override
        protected void onMethodEnter() {
            if(isConstructor) {
                //插入字节码
                mv.visitLdcInsn(Type.getType(HOOK_CLASS));
            }
        }
    }

    private String CLASS_REF_BUG = "com/earthgee/dailytest/hotfix/LoadBugClass";
    private String CLASS_BUG = "com/earthgee/dailytest/hotfix/BugClass";
    private String HOOK_CLASS = "Lcom/earthgee/nuwaref/AntilazyLoad;";

    private boolean isHookClass(String className) {
        className = className.replace(".", "/");
        return className.equals(CLASS_REF_BUG) || className.equals(CLASS_BUG);
    }

}






