package com.earthgee.systrace;


import com.earthgee.systrace.javautil.FileUtil;
import com.earthgee.systrace.javautil.Util;
import com.earthgee.systrace.retrace.MappingCollector;

import java.util.HashSet;

/**
 * 插桩配置项
 */
public class Configuration {

    public String packageName;
    //混淆文件目录
    public String mappingDir;
    //配置项 插桩类表 eg: 7430,1,android.support.v4.widget.SwipeRefreshLayout onNestedFling (Landroid.view.View;FFZ)Z 默认为空
    public String baseMethodMapPath;
    //输出插桩方法表
    //$mappingOut/methodMapping.txt
    public String methodMapFilePath;
    //输出忽略插桩方法表
    //$mappingOut/ignoreMethodMapping.txt
    public String ignoreMethodMapFilePath;
    //配置项 手动声明不插桩类或包
    public String blockListFilePath;
    //agp<4.0使用 对应主工程
    public String traceClassOut;
    public boolean skipCheckClass;
    //插桩白名单（混淆后值 类名 包名）
    public HashSet<String> blockSet = new HashSet<>();

    public Configuration() {
    }

    Configuration(String packageName, String mappingDir, String baseMethodMapPath, String methodMapFilePath,
                  String ignoreMethodMapFilePath, String blockListFilePath, String traceClassOut, boolean skipCheckClass) {
        this.packageName = packageName;
        this.mappingDir = Util.nullAsNil(mappingDir);
        this.baseMethodMapPath = Util.nullAsNil(baseMethodMapPath);
        this.methodMapFilePath = Util.nullAsNil(methodMapFilePath);
        this.ignoreMethodMapFilePath = Util.nullAsNil(ignoreMethodMapFilePath);
        this.blockListFilePath = Util.nullAsNil(blockListFilePath);
        this.traceClassOut = Util.nullAsNil(traceClassOut);
        this.skipCheckClass = skipCheckClass;
    }

    public int parseBlockFile(MappingCollector processor) {
        String blockStr = TraceBuildConstants.DEFAULT_BLOCK_TRACE
                + FileUtil.readFileAsString(blockListFilePath);

        String[] blockArray = blockStr.trim().replace("/", ".").replace("\r", "").split("\n");

        if (blockArray != null) {
            for (String block : blockArray) {
                if (block.length() == 0) {
                    continue;
                }
                if (block.startsWith("#")) {
                    continue;
                }
                if (block.startsWith("[")) {
                    continue;
                }

                if (block.startsWith("-keepclass ")) {
                    block = block.replace("-keepclass ", "");
                    blockSet.add(processor.proguardClassName(block, block));
                } else if (block.startsWith("-keeppackage ")) {
                    block = block.replace("-keeppackage ", "");
                    blockSet.add(processor.proguardPackageName(block, block));
                }
            }
        }
        return blockSet.size();
    }

    @Override
    public String toString() {
        return "\n# Configuration" + "\n"
                + "|* packageName:\t" + packageName + "\n"
                + "|* mappingDir:\t" + mappingDir + "\n"
                + "|* baseMethodMapPath:\t" + baseMethodMapPath + "\n"
                + "|* methodMapFilePath:\t" + methodMapFilePath + "\n"
                + "|* ignoreMethodMapFilePath:\t" + ignoreMethodMapFilePath + "\n"
                + "|* blockListFilePath:\t" + blockListFilePath + "\n"
                + "|* traceClassOut:\t" + traceClassOut + "\n";
    }

    public static class Builder {

        public String packageName;
        public String mappingPath;
        public String baseMethodMap;
        public String methodMapFile;
        public String ignoreMethodMapFile;
        public String blockListFile;
        public String traceClassOut;
        public boolean skipCheckClass = false;

        public Builder setPackageName(String packageName) {
            this.packageName = packageName;
            return this;
        }

        public Builder setMappingPath(String mappingPath) {
            this.mappingPath = mappingPath;
            return this;
        }

        public Builder setBaseMethodMap(String baseMethodMap) {
            this.baseMethodMap = baseMethodMap;
            return this;
        }

        public Builder setTraceClassOut(String traceClassOut) {
            this.traceClassOut = traceClassOut;
            return this;
        }

        public Builder setMethodMapFilePath(String methodMapDir) {
            methodMapFile = methodMapDir;
            return this;
        }

        public Builder setIgnoreMethodMapFilePath(String methodMapDir) {
            ignoreMethodMapFile = methodMapDir;
            return this;
        }

        public Builder setBlockListFile(String blockListFile) {
            this.blockListFile = blockListFile;
            return this;
        }

        public Builder setSkipCheckClass(boolean skipCheckClass) {
            this.skipCheckClass = skipCheckClass;
            return this;
        }

        public Configuration build() {
            return new Configuration(packageName, mappingPath, baseMethodMap, methodMapFile, ignoreMethodMapFile, blockListFile, traceClassOut, skipCheckClass);
        }

    }
}
