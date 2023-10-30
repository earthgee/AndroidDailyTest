package com.earhtgee.systrace.task

import com.android.build.gradle.AppExtension
import com.earhtgee.systrace.compat.MatrixTraceCompat
import com.earhtgee.systrace.extension.MatrixTraceExtension
import org.gradle.api.Project

/**
 * 管理自定义任务
 */
class MatrixTasksManager {

    companion object {
        const val TAG = "Matrix.TasksManager"
    }

    /**
     * 创建相关自定义任务
     * android android命名空间
     * traceExtension trace命名空间
     */
    fun createMatrixTasks(android: AppExtension,
                          project: Project,
                          traceExtension: MatrixTraceExtension) {
        createMatrixTraceTask(android, project, traceExtension)
//        createRemoveUnusedResourcesTask(android, project, removeUnusedResourcesExtension)
    }

    private fun createMatrixTraceTask(
            android: AppExtension,
            project: Project,
            traceExtension: MatrixTraceExtension) {
        MatrixTraceCompat().inject(android, project, traceExtension)
    }

}
