//
// Created by zhaoruixuan1 on 2023/8/10.
//
#include "allocTracker.h"
#include "dlopen.h"
#include "Substrate/SubstrateHook.h"
//#include "HookZz/include/hookzz.h"
#include <pthread.h>
#include <ctime>
#include <cstdlib>
#include <unistd.h>
#include "fb/fbjni/fbjni.h"

pthread_t pthread_self(void);

#ifdef __cplusplus
extern "C" {
#endif
static int randomDouble(double start, double end) {
    return static_cast<int>(start + (end - start) * rand() / (RAND_MAX + 1.0));
}
static int randomInt(int start, int end) {
    return (int) randomDouble(start, end);
}

//private
static void startARTAllocationTracker() {
    LOGI(ALLOC_TRACKER_TAG,
         "art, startAllocationTracker, func==NULL: %s, artEnvSetCheckJniEnabled==NULL: %s",
         artEnvSetCheckJniEnabled == NULL ? "true" : "false",
         artVmSetCheckJniEnabled == NULL ? "true" : "false");

    if (artSetAllocTrackingEnable != NULL) {
        artSetAllocTrackingEnable(true);
    }

    allocObjectCount = {0};
}

static void stopARTAllocationTracker() {
    LOGI("art, stopAllocationTracker, func==NULL: %s, objectCount: %d",
         artSetAllocTrackingEnable == NULL ? "true" : "false", allocObjectCount.load());
    if (artSetAllocTrackingEnable != NULL) {
        artSetAllocTrackingEnable(false);
    }
    allocObjectCount = {0};
}

/**
 * 基本art 都适配 生成的 byte array 格式如下
 * Message header (all values big-endian):
 * (1b) message header len (to allow future expansion); includes itself
 * (1b) entry header len
 * (1b) stack frame len
 * (2b) number of entries
 * (4b) offset to string table from start of message
 * (2b) number of class name strings
 * (2b) number of method name strings
 * (2b) number of source file name strings
 * For each entry:
 *   (4b) total allocation size
 *   (2b) thread id
 *   (2b) allocated object's class name index
 *   (1b) stack depth
 *   For each stack frame:
 *     (2b) method's class name
 *     (2b) method name
 *     (2b) method source file
 *     (2b) line number, clipped to 32767; -2 if native; -1 if no source
 * (xb) class name strings
 * (xb) method name strings
 * (xb) source file strings
 */
jbyteArray getARTAllocationData() {
    if (artGetRecentAllocations != NULL) {

        jbyteArray data = artGetRecentAllocations();
        LOGI("artGetRecentAllocations finished");
        if (data != NULL) {
            LOGI("artGetRecentAllocations success");
            return data;
        } else {
            LOGI("artGetRecentAllocations failed");
        }
    }
    return NULL;
}

bool saveARTAllocationData(SaveAllocationData saveData) {
    JNIEnv *env = facebook::jni::Environment::current();
    {
        snprintf(saveData.dataFileName, 1024, "%s/%d", storeDataDirectory,
                 static_cast<int>(time(0)));
        int fd = open(saveData.dataFileName, O_RDWR | O_CREAT | O_CLOEXEC, (mode_t) 0644);
        lseek(fd, 0, SEEK_SET);
        LOGI("saveARTAllocationData %s file, fd: %d", saveData.dataFileName, fd);
        size_t dataSize = env->GetArrayLength(saveData.data);
        jbyte *olddata = (jbyte *) env->GetByteArrayElements(saveData.data, 0);
        write(fd, olddata, dataSize);
        close(fd);
        LOGI("saveARTAllocationData write file to %s", saveData.dataFileName);
    }
    return true;
}

//private end

/// methods for art
//force dlopen 需要等待 env 构造完成
//static __attribute__((__constructor__))
void hookFunc() {
    LOGI("start hookFunc");
    void *handle = ndk_dlopen("libart.so", RTLD_LAZY | RTLD_GLOBAL);

    if (!handle) {
        LOGE("libart.so open fail");
        return;
    }

    // android 11
    void *hookRecordAllocation30 = ndk_dlsym(handle,
                                             "_ZN3art2gc20AllocRecordObjectMap16RecordAllocationEPNS_6ThreadEPNS_6ObjPtrINS_6mirror6ObjectEEEj");

    if (hookRecordAllocation30 != nullptr) {
        LOGI("Finish get symbol26");
        MSHookFunction(hookRecordAllocation30, (void *) &newArtRecordAllocation30,
                       (void **) &oldArtRecordAllocation30);
    } else {
        LOGI("error find hookRecordAllocation22");
        return;
    }
    dlclose(handle);
}

/**
 * art 初始化
 * apilevel 系统版本
 * allocRecordmax是对象分配数量的最大值
 */
JNI_METHOD_DECL(jint, initForArt)
(JNIEnv *env, jobject jref, jint apiLevel, jint allocRecordMax) {
    srand(unsigned(time(0)));
    allocTrackerType = ALLOC_TRACKER_ART;
    libHandle = ndk_dlopen("libart.so", RTLD_LOCAL);
    if (libHandle == NULL) {
        LOGI("initForArt, dlopen libart failed!");
        return -2;
    }

    LOGI("set allocRecordMax: %d", allocRecordMax);
    setAllocRecordMax = allocRecordMax;

    artDbgDumpRecentAllocations = (void (*)()) (ndk_dlsym(libHandle,
                                                          "_ZN3art3Dbg21DumpRecentAllocationsEv"));

    if (GetDescriptor == nullptr) {
        GetDescriptor = (char *(*)(Class *, std::string *)) ndk_dlsym(libHandle,
                                                                      "_ZN3art6mirror5Class13GetDescriptorEPNSt3__112basic_stringIcNS2_11char_traitsIcEENS2_9allocatorIcEEEE");//根据 class 获取类名
    }

    artSetAllocTrackingEnable = (void (*)(bool)) ndk_dlsym(libHandle,
                                                           "_ZN3art3Dbg23SetAllocTrackingEnabledEb");//开启 alloc tracking 开启成功后才能执行后续操作
    if (artSetAllocTrackingEnable == nullptr) {
        LOGI("find artSetAllocTrackingEnable failed");
    }

    artGetRecentAllocations = (jbyteArray(*)()) ndk_dlsym(libHandle,
                                                          "_ZN3art3Dbg20GetRecentAllocationsEv");//重要方法，dump alloc 里的对象转换成 byte 数据
    if (artGetRecentAllocations == nullptr) {
        LOGI("find artGetRecentAllocations failed");
    }
    artAllocMapClear = (bool (*)(void *)) (ndk_dlsym(libHandle,
                                                     "_ZN3art2gc20AllocRecordObjectMap5ClearEv"));//清理 alloc 中已存在的对象
    if (artAllocMapClear == nullptr) {
        LOGI("find artAllocMapClear failed");
    }

    return JNI_OK;
}

/**
 * start alloc tracker
 */
JNI_METHOD_DECL(void, startAllocationTracker)
(JNIEnv *env, jobject jref) {
    startARTAllocationTracker();
}

/**
 * stop
 */
JNI_METHOD_DECL(void, stopAllocationTracker)
(JNIEnv *env, jobject jref) {
    stopARTAllocationTracker();
}

JNI_METHOD_DECL(void, dumpAllocationDataInLog)
(JNIEnv *env, jobject jref) {
    LOGI("start dumpAllocationDataInLog, allocTrackerType: %d",
         allocTrackerType);
    if (allocTrackerType == ALLOC_TRACKER_ART) {
        if (artDbgDumpRecentAllocations != nullptr) {
            artDbgDumpRecentAllocations();
        }
    }
}

JNI_METHOD_DECL(void, setSaveDataDirectory)
(JNIEnv *env, jobject jref, jstring directory) {
    storeDataDirectory = env->GetStringUTFChars(directory, 0);
}

std::string a;
/**
 * 24以上版本和以下版本上由于 alloc list 的结构不同导致调用方式也不同
 * @param _this
 * @param type
 * @param byte_count
 * @return
 */
static bool newArtRecordAllocationDoing24(void *_this, Class *type, size_t byte_count) {
    if (artAllocMapClear == nullptr) {//如果无法主动 clear 对象，那么下面的逻辑会导致 dump 下来的对象重复
        return false;
    }

    allocObjectCount++;
    char *typeName = GetDescriptor(type, &a);
//    LOGI("=====class name:%s,allocbyte:%d", typeName,byte_count);// 如果只关心分配的对象大小的话，可以不用做alloc dump 的操作
    //达到 max
    int randret = randomInt(0, 100);
    if (randret == LUCKY) {
        LOGI("====current alloc count %d=====", allocObjectCount.load());
        return false;
    }
    if (allocObjectCount > setAllocRecordMax) {
        CMyLock lock(g_Lock);
        allocObjectCount = 0;

        //write alloc data to file
        jbyteArray allocData = getARTAllocationData();
        SaveAllocationData saveData{allocData};
        saveARTAllocationData(saveData);
        if (artAllocMapClear != nullptr) {
            artAllocMapClear(_this);
            LOGI("===========CLEAR ALLOC MAPS=============");
        }

        lock.Unlock();
    }
    return true;
}


#ifdef __cplusplus
}
#endif