package com.earthgee.systrace.extension

class SystraceExtension {
    boolean enable
    String baseMethodMapFile
    String blackListFile
    //build/systrace_output
    String output


    SystraceExtension() {
        enable = true
        baseMethodMapFile = ""
        blackListFile = ""
        output = ""

    }

    @Override
    String toString() {
        """| enable = ${enable}
           | baseMethodMapFile = ${baseMethodMapFile}
           | blackListFile = ${blackListFile}
           | output = ${output}
        """.stripMargin()
    }
}