package com.earhtgee.systrace.compat

enum class AGPVersion(
        val value: String
) {
    // Versions that we cared about.
    AGP_3_6_0("3.6.0"),
    AGP_4_0_0("4.0.0"),
    AGP_4_1_0("4.1.0"),
    AGP_7_0_0("7.0.0")
}

class VersionsCompat {

    companion object {

        @Suppress("DEPRECATION")
        private fun initCurrentAndroidGradlePluginVersion(): String {
            var newVersion = false
            try {
                if (Class.forName("com.android.Version") != null) {
                    newVersion = true
                }
            } catch(t: Throwable) {
            }

            return if (newVersion) {
                com.android.Version.ANDROID_GRADLE_PLUGIN_VERSION
            } else {
                com.android.builder.model.Version.ANDROID_GRADLE_PLUGIN_VERSION
            }
        }

        val androidGradlePluginVersion: String = initCurrentAndroidGradlePluginVersion()

        val lessThan = {agpVersion: AGPVersion -> androidGradlePluginVersion < agpVersion.value }

        val greatThanOrEqual = {agpVersion: AGPVersion -> androidGradlePluginVersion >= agpVersion.value }

        val equalTo = {agpVersion: AGPVersion -> androidGradlePluginVersion.compareTo(agpVersion.value) == 0}
    }

}