package com.earthgee.performance.allocationtracker.tool;

public interface IStackTraceInfo {
    /**
     * Returns the stack trace. This can be <code>null</code>.
     */
    public StackTraceElement[] getStackTrace();
}