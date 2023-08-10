//
// Created by zhaoruixuan1 on 2023/8/10.
//

#ifndef DAILYTEST_ALLOCTRACKER_H
#define DAILYTEST_ALLOCTRACKER_H

#include <dlfcn.h>
#include <jni.h>
#include <stdlib.h>
#include <stdio.h>

#include <sys/mman.h>
#include <fcntl.h>

#include <vector>
#include <string>
#include <atomic>
#include "lock.h"
#include "logger.h"

using std::vector;
using std::atomic;

#define ALLOC_TRACKER_ART 1
#define ALLOC_TRACKER_DVM 2

static unsigned int LUCKY = 50;

#ifdef HAVE_STDINT_H
#include <stdint.h>    /* C99 */
typedef uint8_t             u1;
#else
typedef unsigned char u1;
#endif

#define JNI_METHOD_DECL(ret_type, method_name) \
     extern "C" JNIEXPORT ret_type JNICALL Java_##com_earthgee_performance_allocationtracker##_##AllocTracker##_##method_name

#ifdef __cplusplus
extern "C" {
#endif

struct SaveAllocationData {
    jbyteArray data;
    char dataFileName[128];
};

//lock
static CMutex g_Lock;

static bool needStopRecord = false;
static int allocTrackerType;
static void *libHandle = NULL;
static size_t setAllocRecordMax = 8000;
static atomic<int> allocObjectCount(0);

class Class;
class Thread;
class Object;

__attribute__((always_inline)) inline uint8_t Read1(uintptr_t addr) {
     return *reinterpret_cast<uint8_t *>(addr);
}

__attribute__((always_inline)) inline uint16_t Read2(uintptr_t addr) {
     return *reinterpret_cast<uint16_t *>(addr);
}

__attribute__((always_inline)) inline uint32_t Read4(uintptr_t addr) {
     return *reinterpret_cast<uint32_t *>(addr);
}

__attribute__((always_inline)) inline uint64_t Read8(uintptr_t addr) {
     return *reinterpret_cast<uint64_t *>(addr);
}

__attribute__((always_inline)) inline uintptr_t AccessField(
        uintptr_t addr,
        uint32_t offset) {
     return addr + offset;
}

static char *(*GetDescriptor)(Class *, std::string *) = NULL;
static void (*artSetAllocTrackingEnable)(bool) = NULL;
static jbyteArray (*artGetRecentAllocations)() = NULL;
static bool (*artAllocMapClear)(void *) = NULL;

static void (*artEnvSetCheckJniEnabled)(bool) = NULL;
static bool (*artVmSetCheckJniEnabled)(bool) = NULL;

// dump alloc log in art
static void (*artDbgDumpRecentAllocations)() = NULL;
static const char *storeDataDirectory;

static bool newArtRecordAllocationDoing24(void *, Class *type, size_t byte_count);

//RecordAllocation(Thread* self,
//266                                            ObjPtr<mirror::Object>* obj,
//267                                            size_t byte_count)
static void (*oldArtRecordAllocation30)(void *_this, Thread *, void *obj, size_t);
static void newArtRecordAllocation30(void *_this, Thread *self, void *obj, size_t byte_count) {
     if (needStopRecord) {
          return;
     } else {
          int objptr = Read4(reinterpret_cast<uintptr_t>(obj));//此处获取的其实是一个 ref 对象
          int classRef = Read4(objptr);//根据 ref 获取真实的对象地址
          newArtRecordAllocationDoing24(_this, reinterpret_cast<Class *>(classRef), byte_count);
          oldArtRecordAllocation30(_this, self, obj, byte_count);
     }
}

static jbyteArray getARTAllocationData();

void hookFunc();

#ifdef __cplusplus
}
#endif

#endif //DAILYTEST_ALLOCTRACKER_H
