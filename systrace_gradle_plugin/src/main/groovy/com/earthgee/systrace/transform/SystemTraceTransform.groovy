package com.earthgee.systrace.transform

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformTask
import com.earthgee.systrace.MethodCollector
import com.earthgee.systrace.MethodTracer
import com.earthgee.systrace.TraceBuildConfig
import com.earthgee.systrace.item.TraceMethod
import com.earthgee.systrace.retrace.MappingCollector
import com.earthgee.systrace.retrace.MappingReader
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionGraph
import org.gradle.api.execution.TaskExecutionGraphListener
import com.earthgee.systrace.Util
import java.lang.reflect.Field


public class SystemTraceTransform extends BaseProxyTransform {

    //原始transform
    Transform origTransform
    //项目
    Project project
    //构建变体
    def variant

    SystemTraceTransform(Project project, def variant, Transform origTransform) {
        super(origTransform)
        this.origTransform = origTransform
        this.variant = variant
        this.project = project
    }

    public static void inject(Project project, def variant) {

        //获得hook的任务
        //1.transformClassesWithDexFor${variant.name}
        String hackTransformTaskName = getTransformTaskName(
                 "",
                "",variant.name
        )
        //2.transformClassesWithDexBuilderFor${variant.name}
        String hackTransformTaskNameForWrapper = getTransformTaskName(
                 "",
                "Builder",variant.name
        )
        project.logger.info("prepare inject dex transform :" + hackTransformTaskName +" hackTransformTaskNameForWrapper:"+hackTransformTaskNameForWrapper)

        project.getGradle().getTaskGraph().addTaskExecutionGraphListener(new TaskExecutionGraphListener() {
            @Override
            public void graphPopulated(TaskExecutionGraph taskGraph) {
                for (Task task : taskGraph.getAllTasks()) {
                    //查找hook task
                    if ((task.name.equalsIgnoreCase(hackTransformTaskName) || task.name.equalsIgnoreCase(hackTransformTaskNameForWrapper))
                            && !(((TransformTask) task).getTransform() instanceof SystemTraceTransform)) {
                        project.logger.warn("find dex transform. transform class: " + task.transform.getClass() + " . task name: " + task.name)
                        project.logger.info("variant name: " + variant.name)
                        Field field = TransformTask.class.getDeclaredField("transform")
                        field.setAccessible(true)
                        field.set(task, new SystemTraceTransform(project, variant, task.transform))
                        project.logger.warn("transform class after hook: " + task.transform.getClass())
                        break
                    }
                }
            }
        })
    }

    @Override
    public String getName() {
        return "SystemTraceTransform"
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        long start = System.currentTimeMillis()
        //是否是增量构建
        final boolean isIncremental = transformInvocation.isIncremental() && this.isIncremental()
        //classes/SystemTraceTransform
        final File rootOutput = new File(project.systrace.output, "classes/${getName()}/")
        if (!rootOutput.exists()) {
            rootOutput.mkdirs()
        }
        final TraceBuildConfig traceConfig = initConfig()
        Log.i("Systrace." + getName(), "[transform] isIncremental:%s rootOutput:%s", isIncremental, rootOutput.getAbsolutePath())
        final MappingCollector mappingCollector = new MappingCollector()
        File mappingFile = new File(traceConfig.getMappingPath())
        //如果存在混淆的话 读取混淆文件 转换为对应数据结构
        if (mappingFile.exists() && mappingFile.isFile()) {
            MappingReader mappingReader = new MappingReader(mappingFile)
            mappingReader.read(mappingCollector)
        }

        Map<File, File> jarInputMap = new HashMap<>()
        Map<File, File> scrInputMap = new HashMap<>()

        //反射替换input中的文件为systrace替换后的文件
        transformInvocation.inputs.each { TransformInput input ->
            //目录输入
            input.directoryInputs.each { DirectoryInput dirInput ->
                collectAndIdentifyDir(scrInputMap, dirInput, rootOutput, isIncremental)
            }
            //jar包输入
            input.jarInputs.each { JarInput jarInput ->
                if (jarInput.getStatus() != Status.REMOVED) {
                    collectAndIdentifyJar(jarInputMap, scrInputMap, jarInput, rootOutput, isIncremental)
                }
            }
        }

        MethodCollector methodCollector = new MethodCollector(traceConfig, mappingCollector)
        HashMap<String, TraceMethod> collectedMethodMap = methodCollector.collect(scrInputMap.keySet().toList(), jarInputMap.keySet().toList())
        MethodTracer methodTracer = new MethodTracer(traceConfig, collectedMethodMap, methodCollector.getCollectedClassExtendMap())
        methodTracer.trace(scrInputMap, jarInputMap)
        origTransform.transform(transformInvocation)
        Log.i("Systrace." + getName(), "[transform] cost time: %dms", System.currentTimeMillis() - start)
    }

    /**
     * 目录输入
     * @param dirInputMap 空map key 输入目录 value 输出目录
     * @param input 输入
     * @param rootOutput 输出 classes/SystemTraceTransform
     * @param isIncremental 是否增量编译
     */
    private void collectAndIdentifyDir(Map<File, File> dirInputMap, DirectoryInput input, File rootOutput, boolean isIncremental) {
        final File dirInput = input.file
        //创建对应输出目录 input.file
        final File dirOutput = new File(rootOutput, input.file.getName())
        if (!dirOutput.exists()) {
            dirOutput.mkdirs()
        }
        if (isIncremental) {
            //增量编译比较复杂
            if (!dirInput.exists()) {
                dirOutput.deleteDir()
            } else {
                final Map<File, Status> obfuscatedChangedFiles = new HashMap<>()
                final String rootInputFullPath = dirInput.getAbsolutePath()
                final String rootOutputFullPath = dirOutput.getAbsolutePath()
                input.changedFiles.each { Map.Entry<File, Status> entry ->
                    final File changedFileInput = entry.getKey()
                    final String changedFileInputFullPath = changedFileInput.getAbsolutePath()
                    //输入目录替换为输出目录
                    final File changedFileOutput = new File(
                            changedFileInputFullPath.replace(rootInputFullPath, rootOutputFullPath)
                    )
                    final Status status = entry.getValue()
                    switch (status) {
                        case Status.NOTCHANGED:
                            break
                        case Status.ADDED:
                        case Status.CHANGED:
                            dirInputMap.put(changedFileInput, changedFileOutput)
                            break
                        case Status.REMOVED:
                            //移除该文件
                            changedFileOutput.delete()
                            break
                    }
                    obfuscatedChangedFiles.put(changedFileOutput, status)
                }
                replaceChangedFile(input, obfuscatedChangedFiles)
            }
        } else {
            dirInputMap.put(dirInput, dirOutput)
        }
        replaceFile(input, dirOutput)
    }

    /**
     * jar
     * @param jarInputMaps 空map
     * @param dirInputMaps 目录输入收集映射信息
     * @param input 输入
     * @param rootOutput 输出 classes/SystemTraceTransform
     * @param isIncremental
     */
    private void collectAndIdentifyJar(Map<File, File> jarInputMaps, Map<File, File> dirInputMaps, JarInput input, File rootOutput, boolean isIncremental) {
        final File jarInput = input.file
        final File jarOutput = new File(rootOutput, getUniqueJarName(jarInput))
        if (Util.isRealZipOrJar(jarInput)) {
            switch (input.status) {
                case Status.NOTCHANGED:
                    if (isIncremental) {
                        break
                    }
                case Status.ADDED:
                case Status.CHANGED:
                    jarInputMaps.put(jarInput, jarOutput)
                    break
                case Status.REMOVED:
                    break
            }
        }

        replaceFile(input, jarOutput)
    }


    static
    private String getTransformTaskName(String customDexTransformName, String wrappSuffix, String buildTypeSuffix) {
        if(customDexTransformName != null && customDexTransformName.length() > 0) {
            return customDexTransformName+"For${buildTypeSuffix}"
        }
        return "transformClassesWithDex${wrappSuffix}For${buildTypeSuffix}"
    }

    private TraceBuildConfig initConfig() {
        def configuration = project.systrace
        def variantName = variant.name.capitalize()
        def mappingFilePath = ""
        if (variant.getBuildType().isMinifyEnabled()) {
            //混淆映射文件
            mappingFilePath = variant.mappingFile.getAbsolutePath()
        }
        TraceBuildConfig traceConfig = new TraceBuildConfig.Builder()
                .setPackageName(variant.applicationId)
                .setBaseMethodMap(configuration.baseMethodMapFile)
                .setMethodMapDir(configuration.output + "/${variantName}.methodmap")
                .setIgnoreMethodMapDir(configuration.output + "/${variantName}.ignoremethodmap")
                .setBlackListFile(configuration.blackListFile)
                .setMappingPath(mappingFilePath)
                .build()
        project.logger.info("TraceConfig: " + traceConfig.toString())
        return traceConfig
    }
}
