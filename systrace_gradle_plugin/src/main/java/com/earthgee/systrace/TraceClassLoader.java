package com.earthgee.systrace;

import com.google.common.collect.ImmutableList;
import com.earhtgee.systrace.javautil.Log;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;

/**
 * Created by habbyge on 2019/4/24.
 */
public class TraceClassLoader {

    private static final String TAG = "TraceClassLoader";

    public static URLClassLoader getClassLoader(String compileSdkVersion,
                                                String sdkDirectory, Collection<File> inputFiles)
            throws MalformedURLException {

        ImmutableList.Builder<URL> urls = new ImmutableList.Builder<>();
        File androidJar = getAndroidJar(compileSdkVersion, sdkDirectory);
        if (androidJar != null) {
            Log.i(TAG, "getAndroidJar %s", androidJar.getAbsolutePath());
            urls.add(androidJar.toURI().toURL());
        }

        for (File inputFile : inputFiles) {
            urls.add(inputFile.toURI().toURL());
        }

        ImmutableList<URL> urlImmutableList = urls.build();
        URL[] classLoaderUrls = urlImmutableList.toArray(new URL[urlImmutableList.size()]);
        return new URLClassLoader(classLoaderUrls);
    }

    private static File getAndroidJar(String compileSdkVersion, String sdkDirectory) {
        sdkDirectory = sdkDirectory + File.separator + "platforms" + File.separator;
        String androidJarPath = sdkDirectory + compileSdkVersion + File.separator + "android.jar";
        File androidJar = new File(androidJarPath);
        return androidJar.exists() ? androidJar : null;
    }
}
