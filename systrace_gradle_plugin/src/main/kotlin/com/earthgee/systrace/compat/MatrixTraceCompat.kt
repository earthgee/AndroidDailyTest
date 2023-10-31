package com.earthgee.systrace.compat

import com.android.build.gradle.AppExtension
import com.earthgee.systrace.extension.ITraceSwitchListener
import com.earthgee.systrace.extension.MatrixTraceExtension
import com.earthgee.systrace.trace.MatrixTraceInjection
import org.gradle.api.Project

class MatrixTraceCompat : ITraceSwitchListener {

    companion object {
        const val TAG = "Matrix.TraceCompat"
    }

    var traceInjection: MatrixTraceInjection? = null

    init {
        traceInjection = MatrixTraceInjection()
    }

    override fun onTraceEnabled(enable: Boolean) {
        traceInjection?.onTraceEnabled(enable)
    }

    fun inject(appExtension: AppExtension, project: Project, extension: MatrixTraceExtension) {
        traceInjection?.inject(appExtension, project, extension)
    }
}
