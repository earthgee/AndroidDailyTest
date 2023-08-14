package com.earthgee.systrace;

/**
 * Created by zhaoruixuan1 on 2023/8/11
 * CopyRight (c) haodf.com
 * 功能：
 */
public class ATrace {

    static {
        System.loadLibrary("simulate_systrace");
    }

    private static boolean sHasHook = false;
    private static boolean sHookFailed = false;

    public static synchronized boolean hasHacks() {
        if (!sHasHook && !sHookFailed) {
            sHasHook = installSystraceHook();

            sHookFailed = !sHasHook;
        }
        return sHasHook;
    }

    public static void enableSystrace() {
        if (!hasHacks()) {
            return;
        }

        enableSystraceNative();

        SystraceReflector.updateSystraceTags();
    }

    private static native boolean installSystraceHook();

    private static native void enableSystraceNative();



}
