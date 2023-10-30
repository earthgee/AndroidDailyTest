package com.earthgee.systrace;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.JSRInlinerAdapter;

/**
 * Created by zhaoruixuan1 on 2023/8/24
 * 功能：
 */
public class JSRAdapter extends JSRInlinerAdapter {
    protected JSRAdapter(int api, MethodVisitor methodVisitor, int access, String name, String descriptor, String signature, String[] exceptions) {
        super(api, methodVisitor, access, name, descriptor, signature, exceptions);
    }

}
