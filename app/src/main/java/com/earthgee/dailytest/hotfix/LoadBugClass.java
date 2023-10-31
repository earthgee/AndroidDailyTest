package com.earthgee.dailytest.hotfix;

/**
 * Created by zhaoruixuan1 on 2023/10/31
 * CopyRight (c) haodf.com
 * 功能：
 */
public class LoadBugClass {

    public String getBugString() {
        BugClass bugClass = new BugClass();
        return bugClass.bug();
    }

}
