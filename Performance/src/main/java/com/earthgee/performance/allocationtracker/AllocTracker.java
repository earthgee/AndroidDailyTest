package com.earthgee.performance.allocationtracker;

import androidx.annotation.Keep;

/**
 * Created by zhaoruixuan1 on 2023/8/10
 * CopyRight (c) haodf.com
 * 功能：inline hook
 */
public class AllocTracker {

    static {
        System.loadLibrary("alloc-track-lib");
    }

    @Keep
    public native void startAllocationTracker();

    @Keep
    public native int initForArt(int apiLevel, int allocRecordMax);

    @Keep
    public native void stopAllocationTracker();

    @Keep
    public native void setSaveDataDirectory(String dir);

    @Keep
    public native void dumpAllocationDataInLog();

}
