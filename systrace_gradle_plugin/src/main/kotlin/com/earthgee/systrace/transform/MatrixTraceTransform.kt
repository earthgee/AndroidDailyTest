package com.earthgee.systrace.transform

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import com.earthgee.systrace.Configuration
import com.earthgee.systrace.extension.MatrixTraceExtension
import com.earthgee.systrace.javautil.Log
import com.earthgee.systrace.trace.MatrixTrace
import java.io.File
import java.util.concurrent.ConcurrentHashMap

class MatrixTraceTransform(
    private val compileSdkVersion: String,
    private val sdkDirectory: String,
    private val buildOutputDir: String,
    private val extension: MatrixTraceExtension,
    private var transparent: Boolean = true
) : Transform() {

    companion object {
        const val TAG = "Matrix.TraceTransform"
    }

    //开启transform
    fun enable() {
        transparent = false
    }

    fun disable() {
        transparent = true
    }

    // 指定 Transform 的名称，该名称还会用于组成 Task 的名称
    // 格式为 transform[InputTypes]With[name]For[Configuration]
    override fun getName(): String {
        return "TraceTransform"
    }

    // 指定输入内容类型
    override fun getInputTypes(): Set<QualifiedContent.ContentType> {
        return TransformManager.CONTENT_CLASS
    }

    // 指定消费型输入内容范畴
    override fun getScopes(): MutableSet<in QualifiedContent.Scope>? {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    // 指定是否支持增量编译
    override fun isIncremental(): Boolean {
        return true
    }

    //核心
    override fun transform(transformInvocation: TransformInvocation) {
        super.transform(transformInvocation)
        if (transparent) {
            transparent(transformInvocation)
        } else {
            transforming(transformInvocation)
        }
    }

    /**
     * Passes all inputs throughout.
     * TODO: How to avoid this trivial work?
     */
    private fun transparent(invocation: TransformInvocation) {

        val outputProvider = invocation.outputProvider!!

        if (!invocation.isIncremental) {
            outputProvider.deleteAll()
        }

        for (ti in invocation.inputs) {
            for (jarInput in ti.jarInputs) {
                val inputJar = jarInput.file
                val outputJar = outputProvider.getContentLocation(
                    jarInput.name,
                    jarInput.contentTypes,
                    jarInput.scopes,
                    Format.JAR)

                if (invocation.isIncremental) {
                    when (jarInput.status) {
                        Status.NOTCHANGED -> {
                        }
                        Status.ADDED, Status.CHANGED -> {
                            copyFileAndMkdirsAsNeed(inputJar, outputJar)
                        }
                        Status.REMOVED -> FileUtils.delete(outputJar)
                        else -> {}
                    }
                } else {
                    copyFileAndMkdirsAsNeed(inputJar, outputJar)
                }
            }
            for (directoryInput in ti.directoryInputs) {
                val inputDir = directoryInput.file
                val outputDir = outputProvider.getContentLocation(
                    directoryInput.name,
                    directoryInput.contentTypes,
                    directoryInput.scopes,
                    Format.DIRECTORY)

                if (invocation.isIncremental) {
                    for (entry in directoryInput.changedFiles.entries) {
                        val inputFile = entry.key
                        when (entry.value) {
                            Status.NOTCHANGED -> {
                            }
                            Status.ADDED, Status.CHANGED -> if (!inputFile.isDirectory) {
                                val outputFile = toOutputFile(outputDir, inputDir, inputFile)
                                copyFileAndMkdirsAsNeed(inputFile, outputFile)
                            }
                            Status.REMOVED -> {
                                val outputFile = toOutputFile(outputDir, inputDir, inputFile)
                                FileUtils.deleteIfExists(outputFile)
                            }
                            else -> {}
                        }
                    }
                } else {
                    for (`in` in FileUtils.getAllFiles(inputDir)) {
                        val out = toOutputFile(outputDir, inputDir, `in`)
                        copyFileAndMkdirsAsNeed(`in`, out)
                    }
                }
            }
        }
    }

    private fun copyFileAndMkdirsAsNeed(from: File, to: File) {
        if (from.exists()) {
            to.parentFile.mkdirs()
            FileUtils.copyFile(from, to)
        }
    }

    private fun toOutputFile(outputDir: File, inputDir: File, inputFile: File): File {
        return File(outputDir, inputFile.toRelativeString(inputDir))
    }

    //创建transform 配置文件
    private fun configure(transformInvocation: TransformInvocation): Configuration {
        val buildDir = buildOutputDir
        val dirName = transformInvocation.context.variantName

        val mappingOut = "$buildDir${File.separatorChar}outputs${File.separatorChar}mapping${File.separatorChar}$dirName"

        return Configuration.Builder()
            .setBaseMethodMap(extension.baseMethodMapFile)
            .setBlockListFile(extension.blackListFile)
            .setMethodMapFilePath("$mappingOut/methodMapping.txt")
            .setIgnoreMethodMapFilePath("$mappingOut/ignoreMethodMapping.txt")
            .setMappingPath(mappingOut)
            .setSkipCheckClass(extension.isSkipCheckClass)
            .build()
    }

    //插桩transform
    private fun transforming(invocation: TransformInvocation) {
        Log.i(TAG, "transform start")
        val start = System.currentTimeMillis()
        val outputProvider = invocation.outputProvider!!
        val isIncremental = invocation.isIncremental && this.isIncremental

        if (!isIncremental) {
            outputProvider.deleteAll()
        }

        val config = configure(invocation)

        //变动文件表
        val changedFiles = ConcurrentHashMap<File, Status>()
        //输入转输出表
        val inputToOutput = ConcurrentHashMap<File, File>()
        //输入目录表
        val inputFiles = ArrayList<File>()
        //输出文件目录
        var transformDirectory: File? = null

        for (input in invocation.inputs) {
            for (directoryInput in input.directoryInputs) {
                //目录式输入
                changedFiles.putAll(directoryInput.changedFiles)
                val inputDir = directoryInput.file
                inputFiles.add(inputDir)
                val outputDirectory = outputProvider.getContentLocation(
                    directoryInput.name,
                    directoryInput.contentTypes,
                    directoryInput.scopes,
                    Format.DIRECTORY
                )
                Log.i(
                    TAG,
                    "directoryInput path:%s, outputDirectory path:%s",
                    inputDir.absolutePath,
                    outputDirectory.absolutePath
                )

                inputToOutput[inputDir] = outputDirectory
                if (transformDirectory == null) transformDirectory = outputDirectory.parentFile
            }

            for (jarInput in input.jarInputs) {
                //jar包输入
                val inputFile = jarInput.file
                changedFiles[inputFile] = jarInput.status
                inputFiles.add(inputFile)
                val outputJar = outputProvider.getContentLocation(
                    jarInput.name,
                    jarInput.contentTypes,
                    jarInput.scopes,
                    Format.JAR
                )
                Log.i(
                    TAG,
                    "jarInput name:%s, outputDirectory path:%s",
                    jarInput.name,
                    outputJar.absolutePath
                )

                inputToOutput[inputFile] = outputJar
                if (transformDirectory == null) transformDirectory = outputJar.parentFile
            }
        }

        if (inputFiles.size == 0 || transformDirectory == null) {
            Log.i(TAG, "Matrix trace do not find any input files")
            return
        }

        // Get transform root dir.
        val outputDirectory = transformDirectory
        Log.i(TAG, "outputDirectory name=%s", outputDirectory.name)

        MatrixTrace(
            ignoreMethodMapFilePath = config.ignoreMethodMapFilePath,
            methodMapFilePath = config.methodMapFilePath,
            baseMethodMapPath = config.baseMethodMapPath,
            blockListFilePath = config.blockListFilePath,
            mappingDir = config.mappingDir,
            compileSdkVersion = compileSdkVersion,
            sdkDirectory = sdkDirectory
        ).doTransform(
            classInputs = inputFiles,
            changedFiles = changedFiles,
            isIncremental = isIncremental,
            skipCheckClass = config.skipCheckClass,
            traceClassDirectoryOutput = outputDirectory,
            inputToOutput = inputToOutput,
            legacyReplaceChangedFile = null,
            legacyReplaceFile = null,
            uniqueOutputName = true
        )

        val cost = System.currentTimeMillis() - start
        Log.i(TAG, "Insert matrix trace instrumentations cost time: %sms.", cost)
    }

}
