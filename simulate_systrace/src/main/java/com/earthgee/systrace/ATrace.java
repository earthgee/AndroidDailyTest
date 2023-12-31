package com.earthgee.systrace;

import androidx.annotation.Keep;

/**
 * Created by zhaoruixuan1 on 2023/8/11
 * test
 * 功能：plt hook
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

    @Keep
    private static native boolean installSystraceHook();

    @Keep
    private static native void enableSystraceNative();

    //线程监控
    public static void enableThreadHook() {
        enableThreadHookNative();
    }

    public static void enableSocketHook() {
        enableSocketHookNative();
    }

    @Keep
    private static native void enableThreadHookNative();

    @Keep
    private static native void enableSocketHookNative();

    public static String getStack() {
        return stackTraceToString(new Throwable().getStackTrace());
    }

    private static String stackTraceToString(final StackTraceElement[] arr) {
        if(arr==null) {
            return "";
        }

        StringBuffer sb = new StringBuffer();

        for (StackTraceElement stackTraceElement : arr) {
            String className = stackTraceElement.getClassName();
            // remove unused stacks
            if (className.contains("java.lang.Thread")) {
                continue;
            }

            sb.append(stackTraceElement).append('\n');
        }
        return sb.toString();
    }

}
