package com.earthgee.testcpp

class NativeLib {

    /**
     * A native method that is implemented by the 'testcpp' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {
        // Used to load the 'testcpp' library on application startup.
        init {
            System.loadLibrary("testcpp")
        }
    }
}