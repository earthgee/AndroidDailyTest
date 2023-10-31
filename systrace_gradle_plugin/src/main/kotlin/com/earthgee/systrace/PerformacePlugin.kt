package com.earthgee.systrace

import com.android.build.gradle.AppExtension
import com.earthgee.systrace.extension.MatrixTraceExtension
import com.earthgee.systrace.extension.PerformanceExtension
import com.earthgee.systrace.task.MatrixTasksManager
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware

/**
 *  Created by zhaoruixuan1 on 2023/7/11
 *  功能：性能监控插件
 */
class PerformacePlugin : Plugin<Project> {

    companion object {
        const val TAG = "Plugin"
    }

    override fun apply(project: Project) {
        //嵌套 performance->trace
        val performance = project.extensions.create("performance", PerformanceExtension::class.java)
        val traceExtension = (performance as ExtensionAware).extensions.create(
            "trace",
            MatrixTraceExtension::class.java
        )

        MatrixTasksManager().createMatrixTasks(
            project.extensions.getByName("android") as AppExtension,
            project, traceExtension
        )
    }

}
























