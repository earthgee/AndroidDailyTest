/*
 * Tencent is pleased to support the open source community by making wechat-matrix available.
 * Copyright (C) 2018 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the BSD 3-Clause License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.earthgee.systrace.retrace;

import org.objectweb.asm.Type;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by caichongyang on 2017/8/3.
 */
public class MappingCollector implements MappingProcessor {
    private final static String TAG = "MappingCollector";
    private final static int DEFAULT_CAPACITY = 2000;
    //key 混淆后类名 value 原始类名
    public HashMap<String, String> mObfuscatedRawClassMap = new HashMap<>(DEFAULT_CAPACITY);
    //key 原始类名 value 混淆后类名
    public HashMap<String, String> mRawObfuscatedClassMap = new HashMap<>(DEFAULT_CAPACITY);
    //混淆方法map
    //key 混淆后类名 key 混淆后方法名 Set<MethodInfo> 方法列表 MethodInfo 原始类名 原始方法名
    private final Map<String, Map<String, Set<MethodInfo>>> mObfuscatedClassMethodMap = new HashMap<>();
    //原始方法map
    //key 原始类名 key 原始方法名 Set<MethodInfo> 方法列表 MethodInfo 新类名 新方法名
    private final Map<String, Map<String, Set<MethodInfo>>> mOriginalClassMethodMap = new HashMap<>();

    @Override
    public boolean processClassMapping(String className, String newClassName) {
        this.mObfuscatedRawClassMap.put(newClassName, className);
        this.mRawObfuscatedClassMap.put(className, newClassName);
        return true;
    }

    /**
     * mapping the method name.
     *
     * @param className          the original class name. 原始类名
     * @param methodReturnType   the original external method return type.  原始方法返回类型
     * @param methodName         the original external method name. 原始方法名称
     * @param methodArguments    the original external method arguments. 原始方法参数
     * @param newClassName       the new class name. 新类名（？？？实际使用的还是原始类名）
     * @param newMethodName      the new method name. 映射后方法名
     */
    @Override
    public void processMethodMapping(String className, String methodReturnType, String methodName, String methodArguments, String newClassName, String newMethodName) {
        //获取映射后类名
        newClassName = mRawObfuscatedClassMap.get(className);
        Map<String, Set<MethodInfo>> methodMap = mObfuscatedClassMethodMap.get(newClassName);
        if (methodMap == null) {
            methodMap = new HashMap<>();
            mObfuscatedClassMethodMap.put(newClassName, methodMap);
        }
        Set<MethodInfo> methodSet = methodMap.get(newMethodName);
        if (methodSet == null) {
            methodSet = new LinkedHashSet<>();
            methodMap.put(newMethodName, methodSet);
        }
        methodSet.add(new MethodInfo(className, methodReturnType, methodName, methodArguments));

        Map<String, Set<MethodInfo>> methodMap2 = mOriginalClassMethodMap.get(className);
        if (methodMap2 == null) {
            methodMap2 = new HashMap<>();
            mOriginalClassMethodMap.put(className, methodMap2);
        }
        Set<MethodInfo> methodSet2 = methodMap2.get(methodName);
        if (methodSet2 == null) {
            methodSet2 = new LinkedHashSet<>();
            methodMap2.put(methodName, methodSet2);
        }
        methodSet2.add(new MethodInfo(newClassName, methodReturnType, newMethodName, methodArguments));

    }

    public String originalClassName(String proguardClassName, String defaultClassName) {
        if (mObfuscatedRawClassMap.containsKey(proguardClassName)) {
            return mObfuscatedRawClassMap.get(proguardClassName);
        } else {
            return defaultClassName;
        }
    }

    public String proguardClassName(String originalClassName, String defaultClassName) {
        if (mRawObfuscatedClassMap.containsKey(originalClassName)) {
            return mRawObfuscatedClassMap.get(originalClassName);
        } else {
            return defaultClassName;
        }
    }

    /**
     * get original method info
     *
     * @param obfuscatedClassName 混淆的类名
     * @param obfuscatedMethodName 混淆的方法名
     * @param obfuscatedMethodDesc 混淆的方法描述
     * @return
     */
    public MethodInfo originalMethodInfo(String obfuscatedClassName, String obfuscatedMethodName, String obfuscatedMethodDesc) {
        DescInfo descInfo = parseMethodDesc(obfuscatedMethodDesc, false);

        // obfuscated name -> original method names.
        Map<String, Set<MethodInfo>> methodMap = mObfuscatedClassMethodMap.get(obfuscatedClassName);
        if (methodMap != null) {
            Set<MethodInfo> methodSet = methodMap.get(obfuscatedMethodName);
            if (methodSet != null) {
                // Find all matching methods.
                Iterator<MethodInfo> methodInfoIterator = methodSet.iterator();
                while (methodInfoIterator.hasNext()) {
                    MethodInfo methodInfo = methodInfoIterator.next();
                    if (methodInfo.matches(descInfo.returnType, descInfo.arguments)) {
                        MethodInfo newMethodInfo = new MethodInfo(methodInfo);
                        newMethodInfo.setDesc(descInfo.desc);
                        return newMethodInfo;
                    }
                }
            }
        }

        MethodInfo defaultMethodInfo = MethodInfo.deFault();
        defaultMethodInfo.setDesc(descInfo.desc);
        defaultMethodInfo.setOriginalName(obfuscatedMethodName);
        return defaultMethodInfo;
    }

    /**
     * get obfuscated method info
     *
     * @param originalClassName 原始类名
     * @param originalMethodName 原始方法名
     * @param originalMethodDesc 原始方法签名
     * @return
     */
    public MethodInfo obfuscatedMethodInfo(String originalClassName, String originalMethodName, String originalMethodDesc) {
        //混淆后的方法描述
        DescInfo descInfo = parseMethodDesc(originalMethodDesc, true);

        // Class name -> obfuscated method names.
        Map<String, Set<MethodInfo>> methodMap = mOriginalClassMethodMap.get(originalClassName);
        if (methodMap != null) {
            Set<MethodInfo> methodSet = methodMap.get(originalMethodName);
            if (null != methodSet) {
                // Find all matching methods.
                Iterator<MethodInfo> methodInfoIterator = methodSet.iterator();
                while (methodInfoIterator.hasNext()) {
                    MethodInfo methodInfo = methodInfoIterator.next();
                    MethodInfo newMethodInfo = new MethodInfo(methodInfo);
                    //对methodInfo进行混淆
                    obfuscatedMethodInfo(newMethodInfo);
                    //查找到匹配的方法，赋值desc
                    if (newMethodInfo.matches(descInfo.returnType, descInfo.arguments)) {
                        newMethodInfo.setDesc(descInfo.desc);
                        return newMethodInfo;
                    }
                }
            }
        }
        MethodInfo defaultMethodInfo = MethodInfo.deFault();
        defaultMethodInfo.setDesc(descInfo.desc);
        defaultMethodInfo.setOriginalName(originalMethodName);
        return defaultMethodInfo;
    }

    private void obfuscatedMethodInfo(MethodInfo methodInfo) {
        String methodArguments = methodInfo.getOriginalArguments();
        //参数列表,以,分隔
        String[] args = methodArguments.split(",");
        StringBuffer stringBuffer = new StringBuffer();
        for (String str : args) {
            //只保留类名
            String key = str.replace("[", "").replace("]", "");
            if (mRawObfuscatedClassMap.containsKey(key)) {
                stringBuffer.append(str.replace(key, mRawObfuscatedClassMap.get(key)));
            } else {
                stringBuffer.append(str);
            }
            stringBuffer.append(',');
        }
        if (stringBuffer.length() > 0) {
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        }
        String methodReturnType = methodInfo.getOriginalType();
        String key = methodReturnType.replace("[", "").replace("]", "");
        if (mRawObfuscatedClassMap.containsKey(key)) {
            methodReturnType = methodReturnType.replace(key, mRawObfuscatedClassMap.get(key));
        }
        methodInfo.setOriginalArguments(stringBuffer.toString());
        methodInfo.setOriginalType(methodReturnType);
    }

    /**
     * parse method desc
     * 解析方法签名
     * @param desc
     * @param isRawToObfuscated 是否对结果进行混淆处理
     * @return
     */
    private DescInfo parseMethodDesc(String desc, boolean isRawToObfuscated) {
        DescInfo descInfo = new DescInfo();
        //签名中参数获取
        Type[] argsObj = Type.getArgumentTypes(desc);
        //进行混淆或反混淆的处理
        StringBuffer argumentsBuffer = new StringBuffer();
        StringBuffer descBuffer = new StringBuffer();
        descBuffer.append('(');
        for (Type type : argsObj) {
            String key = type.getClassName().replace("[", "").replace("]", "");
            if (isRawToObfuscated) {
                if (mRawObfuscatedClassMap.containsKey(key)) {
                    argumentsBuffer.append(type.getClassName().replace(key, mRawObfuscatedClassMap.get(key)));
                    descBuffer.append(type.toString().replace(key, mRawObfuscatedClassMap.get(key)));
                } else {
                    argumentsBuffer.append(type.getClassName());
                    descBuffer.append(type.toString());
                }
            } else {
                if (mObfuscatedRawClassMap.containsKey(key)) {
                    argumentsBuffer.append(type.getClassName().replace(key, mObfuscatedRawClassMap.get(key)));
                    descBuffer.append(type.toString().replace(key, mObfuscatedRawClassMap.get(key)));
                } else {
                    argumentsBuffer.append(type.getClassName());
                    descBuffer.append(type.toString());
                }
            }
            argumentsBuffer.append(',');
        }
        descBuffer.append(')');

        Type returnObj;
        try {
            returnObj = Type.getReturnType(desc);
        } catch (ArrayIndexOutOfBoundsException e) {
            returnObj = Type.getReturnType(desc + ";");
        }
        if (isRawToObfuscated) {
            String key = returnObj.getClassName().replace("[", "").replace("]", "");
            if (mRawObfuscatedClassMap.containsKey(key)) {
                descInfo.setReturnType(returnObj.getClassName().replace(key, mRawObfuscatedClassMap.get(key)));
                descBuffer.append(returnObj.toString().replace(key, mRawObfuscatedClassMap.get(key)));
            } else {
                descInfo.setReturnType(returnObj.getClassName());
                descBuffer.append(returnObj.toString());
            }
        } else {
            String key = returnObj.getClassName().replace("[", "").replace("]", "");
            if (mObfuscatedRawClassMap.containsKey(key)) {
                descInfo.setReturnType(returnObj.getClassName().replace(key, mObfuscatedRawClassMap.get(key)));
                descBuffer.append(returnObj.toString().replace(key, mObfuscatedRawClassMap.get(key)));
            } else {
                descInfo.setReturnType(returnObj.getClassName());
                descBuffer.append(returnObj.toString());
            }
        }

        // delete last ,
        if (argumentsBuffer.length() > 0) {
            argumentsBuffer.deleteCharAt(argumentsBuffer.length() - 1);
        }
        descInfo.setArguments(argumentsBuffer.toString());

        descInfo.setDesc(descBuffer.toString());
        return descInfo;
    }

    /**
     * about method desc info
     * 进行过混淆或反混淆处理
     */
    private static class DescInfo {
        //描述
        private String desc;
        //参数列表,以,分隔
        private String arguments;
        //返回类型
        private String returnType;

        public void setArguments(String arguments) {
            this.arguments = arguments;
        }

        public void setReturnType(String returnType) {
            this.returnType = returnType;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }

}
