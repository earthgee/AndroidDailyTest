package com.earthgee.systrace;

import android.os.Trace;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by zhaoruixuan1 on 2023/8/14
 * CopyRight (c) haodf.com
 * 功能：
 */
public class SystraceReflector {

    public static final void updateSystraceTags() {
        if (sTrace_sEnabledTags != null && sTrace_nativeGetEnabledTags != null) {
            try {
                sTrace_sEnabledTags.set(null, sTrace_nativeGetEnabledTags.invoke(null));
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {

            }
        }
    }

    private static final Method sTrace_nativeGetEnabledTags;
    private static final Field sTrace_sEnabledTags;

    static {
        Method m;
        try {
            m = Trace.class.getDeclaredMethod("nativeGetEnabledTags");
            m.setAccessible(true);
        } catch (NoSuchMethodException e) {
            m = null;
        }
        sTrace_nativeGetEnabledTags = m;

        Field f;
        try {
            f = Trace.class.getDeclaredField("sEnabledTags");
            f.setAccessible(true);
        } catch (NoSuchFieldException e) {
            f = null;
        }
        sTrace_sEnabledTags = f;
    }

}
