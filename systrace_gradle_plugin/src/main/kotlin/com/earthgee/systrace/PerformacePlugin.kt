package com.earhtgee.systrace

import com.android.build.gradle.AppExtension
import com.earhtgee.systrace.task.MatrixTasksManager
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 *  Created by zhaoruixuan1 on 2023/7/11
 *  功能：gradle插件
 */
class PerformacePlugin : Plugin<Project> {

    companion object {
        const val TAG = "Plugin"
    }

    override fun apply(project: Project) {
        //嵌套 performance->trace
//        val performance = project.extensions.create("performance", PerformanceExtension::class.java)
//        val traceExtension = (performance as ExtensionAware).extensions.create(
//            "trace",
//            MatrixTraceExtension::class.java
//        )

        MatrixTasksManager().createMatrixTasks(
            project.extensions.getByName("android") as AppExtension,
            project
        )
    }

}
























