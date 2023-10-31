package com.earthgee.systrace.extension;

/**
 * Created by zhaoruixuan1 on 2023/7/11
 * 功能：性能插件二级扩展 name:trace
 */
public class MatrixTraceExtension {

    boolean transformInjectionForced;
    //插桩类表
    String baseMethodMapFile;
    //手动指定不插桩的类
    String blackListFile;
    String customDexTransformName;
    boolean skipCheckClass = true; // skip by default

    boolean enable;

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public void setBlackListFile(String blackListFile) {
        this.blackListFile = blackListFile;
    }

    public void setCustomDexTransformName(String customDexTransformName) {
        this.customDexTransformName = customDexTransformName;
    }

    public void setBaseMethodMapFile(String baseMethodMapFile) {
        this.baseMethodMapFile = baseMethodMapFile;
    }

    public void setTransformInjectionForced(boolean transformInjectionForced) {
        this.transformInjectionForced = transformInjectionForced;
    }

    public void setSkipCheckClass(boolean skipCheckClass) {
        this.skipCheckClass = skipCheckClass;
    }

    public String getBaseMethodMapFile() {
        return baseMethodMapFile;
    }

    public String getBlackListFile() {
        return blackListFile;
    }

    public String getCustomDexTransformName() {
        return customDexTransformName;
    }

    public boolean isTransformInjectionForced() {
        return transformInjectionForced;
    }

    public boolean isEnable() {
        return enable;
    }

    public boolean isSkipCheckClass() {
        return skipCheckClass;
    }

}
