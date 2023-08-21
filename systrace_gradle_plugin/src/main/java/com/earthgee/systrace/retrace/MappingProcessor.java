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

/**
 * Created by caichongyang on 2017/6/3.
 */
public interface MappingProcessor {
    /**
     * mapping the class name.
     *
     * @param className    the original class name.
     * @param newClassName the new class name.
     * @return whether the processor is interested in receiving mappings of the class members of
     * this class.
     */
    boolean processClassMapping(String className,
                                       String newClassName);

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
    void processMethodMapping(String className,
                                     String methodReturnType,
                                     String methodName,
                                     String methodArguments,
                                     String newClassName,
                                     String newMethodName);
}
