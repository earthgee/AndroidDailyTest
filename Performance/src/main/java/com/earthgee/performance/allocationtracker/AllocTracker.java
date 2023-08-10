package com.earthgee.performance.allocationtracker;

/**
 * Created by zhaoruixuan1 on 2023/8/10
 * CopyRight (c) haodf.com
 * 功能：
 */
public class AllocTracker {

    static {
        System.loadLibrary("alloc-track-lib");
    }

    public native void startAllocationTracker();

    public native int initForArt(int apiLevel, int allocRecordMax);

    public native void stopAllocationTracker();

    public native void setSaveDataDirectory(String dir);

    public native void dumpAllocationDataInLog();

}
