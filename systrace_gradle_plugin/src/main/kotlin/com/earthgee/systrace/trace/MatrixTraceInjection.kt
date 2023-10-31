package com.earthgee.systrace.trace

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.BaseVariant
import com.android.builder.model.CodeShrinker
import com.earthgee.systrace.compat.CreationConfig.Companion.getCodeShrinker
import com.earthgee.systrace.extension.ITraceSwitchListener
import com.earthgee.systrace.extension.MatrixTraceExtension
import com.earthgee.systrace.javautil.Log
import com.earthgee.systrace.transform.MatrixTraceTransform
import org.gradle.api.Project
import org.gradle.api.Task

class MatrixTraceInjection : ITraceSwitchListener {

    companion object {
        const val TAG = "Matrix.TraceInjection"
    }

    private var traceEnable = false

    override fun onTraceEnabled(enable: Boolean) {
        traceEnable = enable
    }

    /**
     * android android命名空间
     * traceExtension trace命名空间
     */
    fun inject(appExtension: AppExtension,
               project: Project,
               extension: MatrixTraceExtension
    ) {
        Log.i(TAG, "agp level greater than 4.0,inject")
        injectTransparentTransform(appExtension, project, extension)
        project.afterEvaluate {
            if (extension.isEnable) {
                doInjection(appExtension, project, extension)
            }
        }
    }

    private var transparentTransform: MatrixTraceTransform? = null

    //注册transform
    private fun injectTransparentTransform(appExtension: AppExtension,
                                           project: Project,
                                           extension: MatrixTraceExtension) {
        transparentTransform = MatrixTraceTransform(appExtension.compileSdkVersion?: "",
            appExtension.sdkDirectory.absolutePath,
            project.buildDir.absolutePath, extension)
        appExtension.registerTransform(transparentTransform!!)
    }

    //执行注入逻辑
    private fun doInjection(appExtension: AppExtension,
                            project: Project,
                            extension: MatrixTraceExtension) {
        appExtension.applicationVariants.all { variant ->
            Log.i(TAG, "variant buildType name:"+variant.buildType.name)
            if (injectTaskOrTransform(project, extension, variant) == InjectionMode.TransformInjection) {
                // Inject transform
                Log.i(TAG, "doInjection, inject transform")
                transformInjection()
            } else {
                // Inject task
                Log.i(TAG, "doInjection, inject task empty")
                //taskInjection(project, extension, variant)
            }
        }
    }

//    private fun taskInjection(project: Project,
//                              extension: MatrixTraceExtension,
//                              variant: BaseVariant) {
//
////        Log.i(TAG, "Using trace task mode.")
//
//        project.afterEvaluate {
//
//            val creationConfig = CreationConfig(variant, project)
//            val action = MatrixTraceTask.CreationAction(creationConfig, extension)
//            val traceTaskProvider = project.tasks.register(action.name, action.type, action)
//
//            val variantName = variant.name
//
//            val minifyTasks = arrayOf(
//                    BaseCreationAction.computeTaskName("minify", variantName, "WithProguard")
//            )
//
//            var minify = false
//            for (taskName in minifyTasks) {
//                val taskProvider = BaseCreationAction.findNamedTask(project.tasks, taskName)
//                if (taskProvider != null) {
//                    minify = true
//                    traceTaskProvider.dependsOn(taskProvider)
//                }
//            }
//
//            if (minify) {
//                val dexBuilderTaskName = BaseCreationAction.computeTaskName("dexBuilder", variantName, "")
//                val taskProvider = BaseCreationAction.findNamedTask(project.tasks, dexBuilderTaskName)
//
//                taskProvider?.configure { task: Task ->
//                    traceTaskProvider.get().wired(creationConfig, task as DexArchiveBuilderTask)
//                }
//
////                if (taskProvider == null) {
////                    Log.e(TAG, "Do not find '$dexBuilderTaskName' task. Inject matrix trace task failed.")
////                }
//            }
//        }
//    }

    private fun transformInjection() {
        transparentTransform!!.enable()
    }

    enum class InjectionMode {
        TaskInjection,
        TransformInjection,
    }

    private fun injectTaskOrTransform(project: Project,
                                      extension: MatrixTraceExtension,
                                      variant: BaseVariant): InjectionMode {

        if (!variant.buildType.isMinifyEnabled
                || extension.isTransformInjectionForced
                || getCodeShrinker(project) == CodeShrinker.R8
        ) {
            return InjectionMode.TransformInjection
        }

        return InjectionMode.TaskInjection
    }


}
