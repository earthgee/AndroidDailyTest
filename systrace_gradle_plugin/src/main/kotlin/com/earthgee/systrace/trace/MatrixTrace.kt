/*
 * Tencent is pleased to support the open source community by making wechat-matrix available.
 * Copyright (C) 2018 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the BSD 3-Clause License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.earhtgee.systrace.trace

import com.android.build.api.transform.Status
import com.android.utils.FileUtils
import com.google.common.hash.Hashing
import com.earhtgee.systrace.*
import com.earhtgee.systrace.item.TraceMethod
import com.earhtgee.systrace.javautil.IOUtil
import com.earhtgee.systrace.javautil.Log
import com.earhtgee.systrace.javautil.Util
import com.earhtgee.systrace.retrace.MappingCollector
import com.earhtgee.systrace.retrace.MappingReader

import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MatrixTrace(
        //混淆忽略表
        private val ignoreMethodMapFilePath: String,
        //混淆映射表
        private val methodMapFilePath: String,
        //配置插桩表
        private val baseMethodMapPath: String?,
        //配置插桩白名单
        private val blockListFilePath: String?,
        private val mappingDir: String,
        private val compileSdkVersion: String,
        private val sdkDirectory: String
) {
    companion object {
        private const val TAG: String = "Matrix.Trace"

        @Suppress("DEPRECATION")
        fun getUniqueJarName(jarFile: File): String {
            val origJarName = jarFile.name
            val hashing = Hashing.sha1().hashString(jarFile.path, Charsets.UTF_16LE).toString()
            val dotPos = origJarName.lastIndexOf('.')
            return if (dotPos < 0) {
                String.format("%s_%s", origJarName, hashing)
            } else {
                val nameWithoutDotExt = origJarName.substring(0, dotPos)
                val dotExt = origJarName.substring(dotPos)
                String.format("%s_%s%s", nameWithoutDotExt, hashing, dotExt)
            }
        }

        fun appendSuffix(jarFile: File, suffix: String): String {
            val origJarName = jarFile.name
            val dotPos = origJarName.lastIndexOf('.')
            return if (dotPos < 0) {
                String.format("%s_%s", origJarName, suffix)
            } else {
                val nameWithoutDotExt = origJarName.substring(0, dotPos)
                val dotExt = origJarName.substring(dotPos)
                String.format("%s_%s%s", nameWithoutDotExt, suffix, dotExt)
            }
        }

    }

    /**
     * classInputs:输入文件（目录输入和jar包输入）
     * changedFiles:文件状态表
     * inputToOutput:输入输出映射表
     * isIncremental:是否增量编译
     * skipCheckClass
     * traceClassDirectoryOutput:输出root dir
     */
    fun doTransform(classInputs: Collection<File>,
                    changedFiles: Map<File, Status>,
                    inputToOutput: Map<File, File>,
                    isIncremental: Boolean,
                    skipCheckClass: Boolean,
                    traceClassDirectoryOutput: File,
                    legacyReplaceChangedFile: ((File, Map<File, Status>) -> Object)?,
                    legacyReplaceFile: ((File, File) -> (Object))?,
                    uniqueOutputName: Boolean
    ) {
        //并发任务
        val executor: ExecutorService = Executors.newFixedThreadPool(16)

        val config = Configuration.Builder()
                .setIgnoreMethodMapFilePath(ignoreMethodMapFilePath)
                .setMethodMapFilePath(methodMapFilePath)
                .setBaseMethodMap(baseMethodMapPath)
                .setBlockListFile(blockListFilePath)
                .setMappingPath(mappingDir)
                .setSkipCheckClass(skipCheckClass)
                .build()

        /**
         * step 1 根据上一次的插桩表和class输入解析出当次插桩表
         */
        var start = System.currentTimeMillis()
        Log.i(TAG, "start doTransform:$start")

        val futures = LinkedList<Future<*>>()

        val mappingCollector = MappingCollector()
        //插桩表 methodId 自增
        val methodId = AtomicInteger(0)
        //key 方法名 value 混淆过的方法名称
        val collectedMethodMap = ConcurrentHashMap<String, TraceMethod>()

        futures.add(executor.submit(ParseMappingTask(
                mappingCollector, collectedMethodMap, methodId, config)))

        val dirInputOutMap = ConcurrentHashMap<File, File>()
        val jarInputOutMap = ConcurrentHashMap<File, File>()

        for (file in classInputs) {
            if (file.isDirectory) {
                //目录输入
                futures.add(executor.submit(CollectDirectoryInputTask(
                        directoryInput = file,
                        mapOfChangedFiles = changedFiles,
                        mapOfInputToOutput = inputToOutput,
                        isIncremental = isIncremental,
                        traceClassDirectoryOutput = traceClassDirectoryOutput,
                        legacyReplaceChangedFile = legacyReplaceChangedFile,
                        legacyReplaceFile = legacyReplaceFile,

                        // result
                        resultOfDirInputToOut = dirInputOutMap
                )))
            } else {
                //jar输入
                val status = Status.CHANGED
                futures.add(executor.submit(CollectJarInputTask(
                        inputJar = file,
                        inputJarStatus = status,
                        inputToOutput = inputToOutput,
                        isIncremental = isIncremental,
                        traceClassFileOutput = traceClassDirectoryOutput,
                        legacyReplaceFile = legacyReplaceFile,
                        uniqueOutputName = uniqueOutputName,

                        // result
                        resultOfDirInputToOut = dirInputOutMap,
                        resultOfJarInputToOut = jarInputOutMap
                )))
            }
        }

        //同步屏障
        for (future in futures) {
            future.get()
        }
        futures.clear()

        Log.i(TAG, "[doTransform] Step(1)[Parse]... cost:%sms", System.currentTimeMillis() - start)

        /**
         * step 2
         */
        start = System.currentTimeMillis()
        val methodCollector = MethodCollector(executor, mappingCollector, methodId, config, collectedMethodMap)

        methodCollector.collect(dirInputOutMap.keys, jarInputOutMap.keys)
        Log.i(TAG, "[doTransform] Step(2)[Collection]... cost:%sms", System.currentTimeMillis() - start)

        /**
         * step 3
         */
        start = System.currentTimeMillis()
        val methodTracer = MethodTracer(executor, mappingCollector, config, methodCollector.collectedMethodMap, methodCollector.collectedClassExtendMap)
        val allInputs = ArrayList<File>().also {
            it.addAll(dirInputOutMap.keys)
            it.addAll(jarInputOutMap.keys)
        }
        val traceClassLoader = TraceClassLoader.getClassLoader(compileSdkVersion, sdkDirectory, allInputs)
        methodTracer.trace(dirInputOutMap, jarInputOutMap, traceClassLoader, skipCheckClass)
        traceClassLoader.close()
        Log.i(TAG, "[doTransform] Step(3)[Trace]... cost:%sms", System.currentTimeMillis() - start)
    }

    //解析映射任务
    class ParseMappingTask
    constructor(
            private val mappingCollector: MappingCollector,
            private val collectedMethodMap: ConcurrentHashMap<String, TraceMethod>,
            private val methodId: AtomicInteger,
            private val config: Configuration
    ) : Runnable {

        override fun run() {
            val start = System.currentTimeMillis()

            val mappingFile = File(config.mappingDir, "mapping.txt")
            if (mappingFile.isFile) {
                //存在混淆映射文件,解析并保存retrace相关信息
                val mappingReader = MappingReader(mappingFile)
                mappingReader.read(mappingCollector)
            }
            val size = config.parseBlockFile(mappingCollector)

            val baseMethodMapFile = File(config.baseMethodMapPath)
            //解析增量插桩表文件
            getMethodFromBaseMethod(baseMethodMapFile, collectedMethodMap)
            //retrace 原始->混淆
            retraceMethodMap(mappingCollector, collectedMethodMap)

            Log.i(TAG, "[ParseMappingTask#run] cost:%sms, black size:%s, collect %s method from %s",
                    System.currentTimeMillis() - start, size, collectedMethodMap.size, config.baseMethodMapPath)
        }

        private fun retraceMethodMap(
                processor: MappingCollector,
                methodMap: ConcurrentHashMap<String, TraceMethod>) {
            val retraceMethodMap = HashMap<String, TraceMethod>(methodMap.size)
            for (traceMethod in methodMap.values) {
                traceMethod.proguard(processor)
                retraceMethodMap[traceMethod.getMethodName()] = traceMethod
            }
            methodMap.clear()
            methodMap.putAll(retraceMethodMap)
            retraceMethodMap.clear()
        }

        private fun getMethodFromBaseMethod(
                baseMethodFile: File,
                collectedMethodMap: ConcurrentHashMap<String, TraceMethod>) {
            if (!baseMethodFile.exists()) {
                Log.w(TAG, "[getMethodFromBaseMethod] not exist!%s", baseMethodFile.absolutePath)
                return
            }

            try {
                Scanner(baseMethodFile, "UTF-8").use { fileReader ->
                    while (fileReader.hasNext()) {
                        var nextLine = fileReader.nextLine()
                        if (!Util.isNullOrNil(nextLine)) {
                            nextLine = nextLine.trim()
                            if (nextLine.startsWith("#")) {
                                Log.i("[getMethodFromBaseMethod] comment %s", nextLine)
                                continue
                            }
                            val fields = nextLine.split(",")
                            val traceMethod = TraceMethod()
                            traceMethod.id = Integer.parseInt(fields[0])
                            traceMethod.accessFlag = Integer.parseInt(fields[1])
                            val methodField = fields[2].split(" ")
                            traceMethod.className = methodField[0].replace("/", ".")
                            traceMethod.methodName = methodField[1]
                            if (methodField.size > 2) {
                                traceMethod.desc = methodField[2].replace("/", ".")
                            }
                            collectedMethodMap[traceMethod.getMethodName()] = traceMethod
                            if (methodId.get() < traceMethod.id && traceMethod.id != TraceBuildConstants.METHOD_ID_DISPATCH) {
                                methodId.set(traceMethod.id)
                            }

                        }
                    }
                }
            } catch (e: Throwable) {
                Log.printErrStackTrace(TAG, e, "")
            }
        }
    }


    class CollectDirectoryInputTask(
            //输入目录
            private val directoryInput: File,
            //更新文件及状态
            private val mapOfChangedFiles: Map<File, Status>,
            //输入目录对应输出目录
            private val mapOfInputToOutput: Map<File, File>,
            private val isIncremental: Boolean,
            //输出根目录
            private val traceClassDirectoryOutput: File,
            private val legacyReplaceChangedFile: ((File, Map<File, Status>) -> (Object))?,     // Will be removed in the future
            private val legacyReplaceFile: ((File, File) -> (Object))?,                         // Will be removed in the future

            private val resultOfDirInputToOut: MutableMap<File, File>
    ) : Runnable {

        override fun run() {
            try {
                handle()
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(TAG, "%s", e.toString())
            }
        }

        private fun handle() {
            val dirInput = directoryInput
            val dirOutput = if (mapOfInputToOutput.containsKey(dirInput)) {
                //agp>=4.0
                mapOfInputToOutput[dirInput]!!
            } else {
                File(traceClassDirectoryOutput, dirInput.name)
            }
            val inputFullPath = dirInput.absolutePath
            val outputFullPath = dirOutput.absolutePath

            if (!dirOutput.exists()) {
                dirOutput.mkdirs()
            }

            if (!dirInput.exists() && dirOutput.exists()) {
                if (dirOutput.isDirectory) {
                    FileUtils.deletePath(dirOutput)
                } else {
                    FileUtils.delete(dirOutput)
                }
            }

            if (isIncremental) {
                val outChangedFiles = HashMap<File, Status>()

                for ((changedFileInput, status) in mapOfChangedFiles) {
                    val changedFileInputFullPath = changedFileInput.absolutePath

                    // mapOfChangedFiles is contains all. each collectDirectoryInputTask should handle itself, should not handle other file
                    if (!changedFileInputFullPath.contains(inputFullPath)) {
                        continue
                    }

                    val changedFileOutput = File(changedFileInputFullPath.replace(inputFullPath, outputFullPath))

                    if (status == Status.ADDED || status == Status.CHANGED) {
                        resultOfDirInputToOut[changedFileInput] = changedFileOutput
                    } else if (status == Status.REMOVED) {
                        changedFileOutput.delete()
                    }
                    outChangedFiles[changedFileOutput] = status
                }

                legacyReplaceChangedFile?.invoke(dirInput, outChangedFiles)
            } else {
                resultOfDirInputToOut[dirInput] = dirOutput
            }

            legacyReplaceFile?.invoke(dirInput, dirOutput)
        }
    }

    class CollectJarInputTask(
            //输入jar目录
            private val inputJar: File,
            private val inputJarStatus: Status,
            //输入转换目录 输入->输出
            private val inputToOutput: Map<File, File>,
            private val isIncremental: Boolean,
            private val traceClassFileOutput: File,
            private val legacyReplaceFile: ((File, File) -> (Object))?,             // Will be removed in the future
            private val uniqueOutputName: Boolean,
            private val resultOfDirInputToOut: MutableMap<File, File>,
            private val resultOfJarInputToOut: MutableMap<File, File>
    ) : Runnable {

        override fun run() {
            try {
                handle()
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(TAG, "%s", e.toString())
            }
        }

        private fun handle() {
            val jarInput = inputJar
            val jarOutput = if (inputToOutput.containsKey(jarInput)) {
                inputToOutput[jarInput]!!
            } else {
                val outputJarName = if (uniqueOutputName)
                    getUniqueJarName(jarInput)
                else
                    appendSuffix(jarInput, "traced")
                File(traceClassFileOutput, outputJarName)
            }

            Log.d(TAG, "CollectJarInputTask input %s -> output %s", jarInput, jarOutput)

            if (!isIncremental && jarOutput.exists()) {
                jarOutput.delete()
            }
            if (!jarOutput.parentFile.exists()) {
                jarOutput.parentFile.mkdirs()
            }

            if (IOUtil.isRealZipOrJar(jarInput)) {
                if (isIncremental) {
                    if (inputJarStatus == Status.ADDED || inputJarStatus == Status.CHANGED) {
                        resultOfJarInputToOut[jarInput] = jarOutput
                    } else if (inputJarStatus == Status.REMOVED) {
                        jarOutput.delete()
                    }

                } else {
                    resultOfJarInputToOut[jarInput] = jarOutput
                }

            }

            legacyReplaceFile?.invoke(jarInput, jarOutput)
        }
    }


}