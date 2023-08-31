package com.earthgee.systrace

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import com.earthgee.systrace.extension.SystraceExtension
import com.earthgee.systrace.transform.SystemTraceTransform

/**
 * Created by zhangshaowen on 17/6/16.
 */
class SystracePlugin implements Plugin<Project> {
    private static final String TAG = "SystracePlugin"

    @Override
    void apply(Project project) {
        project.extensions.create("systrace", SystraceExtension)

        if (!project.plugins.hasPlugin('com.android.application')) {
            throw new GradleException('Systrace Plugin, Android Application plugin required')
        }

        project.afterEvaluate {
            def android = project.extensions.android
            def configuration = project.systrace
            //遍历所有应用变体
            android.applicationVariants.all { variant ->
                String output = configuration.output
                if (Util.isNullOrNil(output)) {
                    configuration.output = project.getBuildDir().getAbsolutePath() + File.separator + "systrace_output"
                    Log.i(TAG, "set Systrace output file to " + configuration.output)
                }

                Log.i(TAG, "Trace enable is %s", configuration.enable)
                if (configuration.enable) {
                    SystemTraceTransform.inject(project, variant)
                }
            }
        }
    }
}