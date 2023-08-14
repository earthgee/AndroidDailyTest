//
// Created by zhaoruixuan1 on 2023/8/11.
// atrace
#include <jni.h>
#include <string>

#include <atomic>
#include <dlfcn.h>
#include <sys/mman.h>
#include <unistd.h>
#include <sstream>
#include <unordered_set>
#include <android/log.h>
#include <fcntl.h>
#include <sys/fcntl.h>
#include <stdlib.h>
#include <libgen.h>
#include <sys/system_properties.h>
#include <vector>
#include <syscall.h>
#include "linker.h"
#include "hooks.h"

#define JNI_METHOD_DECL(ret_type, method_name) \
     extern "C" JNIEXPORT ret_type JNICALL Java_##com_earthgee_systrace##_##ATrace##_##method_name

#define  LOG_TAG    "HOOOOOOOOK"
#define  ALOG(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
static const int64_t kSecondNanos = 1000000000;

std::atomic<uint64_t> *atrace_enabled_tags = nullptr;
int *atrace_marker_fd = nullptr;
std::atomic<bool> systrace_installed;
bool first_enable = true;
std::atomic<uint64_t> original_tags(UINT64_MAX);

/**
* 只针对特定的fd，降低性能影响
* @param fd
* @param count
* @return
*/
bool should_log_systrace(int fd, size_t count) {
    return systrace_installed && fd == *atrace_marker_fd && count > 0;
}

void log_systrace(const void *buf, size_t count) {
    const char *msg = reinterpret_cast<const char *>(buf);

    switch (msg[0]) {

        case 'B': { // begin synchronous event. format: "B|<pid>|<name>"
            ALOG("========= %s", msg);
            break;
        }
        case 'E': { // end synchronous event. format: "E"
            ALOG("========= E");
            break;
        }
            // the following events we don't currently log.
        case 'S': // start async event. format: "S|<pid>|<name>|<cookie>"
        case 'F': // finish async event. format: "F|<pid>|<name>|<cookie>"
        case 'C': // counter. format: "C|<pid>|<name>|<value>"
        default:
            return;
    }
}

ssize_t write_hook(int fd, const void *buf, size_t count) {
    if (should_log_systrace(fd, count)) {
        log_systrace(buf, count);
        return count;
    }
    return CALL_PREV(write_hook, fd, buf, count);
}

ssize_t __write_chk_hook(int fd, const void *buf, size_t count, size_t buf_size) {
    if (should_log_systrace(fd, count)) {
        log_systrace(buf, count);
        return count;
    }
    return CALL_PREV(__write_chk_hook, fd, buf, count, buf_size);
}

/**
* plt hook libc 的 write 方法
*/
void hookLoadedLibs() {
    hook_plt_method("libc.so", "write", (hook_func) &write_hook);
    hook_plt_method("libc.so", "__write_chk", (hook_func) &__write_chk_hook);
}

void installSystraceSnooper() {
    {
        std::string lib_name("libcutils.so");
        std::string enabled_tags_sym("atrace_enabled_tags");
        std::string fd_sym("atrace_marker_fd");

        void *handle;
        handle = dlopen(nullptr, RTLD_GLOBAL);

        atrace_enabled_tags =
                reinterpret_cast<std::atomic<uint64_t> *>(
                        dlsym(handle, enabled_tags_sym.c_str()));

        if (atrace_enabled_tags == nullptr) {
            throw std::runtime_error("Enabled Tags not defined");
        }

        atrace_marker_fd =
                reinterpret_cast<int *>(dlsym(handle, fd_sym.c_str()));

        if (atrace_marker_fd == nullptr) {
            throw std::runtime_error("Trace FD not defined");
        }
        if (*atrace_marker_fd == -1) {
            throw std::runtime_error("Trace FD not valid");
        }
    }

    if (linker_initialize()) {
        throw std::runtime_error("Could not initialize linker library");
    }

    hookLoadedLibs();

    systrace_installed = true;
}

bool installSystraceHook() {
    try {
        ALOG("===============install systrace hoook==================");
        installSystraceSnooper();
        return true;
    } catch (const std::runtime_error &e) {
        return false;
    }
}

void enableSystrace() {
    if (!systrace_installed) {
        return;
    }

    if (!first_enable) {
        // On every enable, except the first one, find if new libs were loaded
        // and install systrace hook for them
        try {
            hookLoadedLibs();
        } catch (...) {
            // It's ok to continue if the refresh has failed
        }
    }
    first_enable = false;

    //所有位置1，使能开关打开
    auto prev = atrace_enabled_tags->exchange(UINT64_MAX);
    if (prev !=
        UINT64_MAX) { // if we somehow call this twice in a row, don't overwrite the real tags
        original_tags = prev;
    }
}

JNI_METHOD_DECL(jboolean, installSystraceHook)
(JNIEnv *env, jclass type) {
    installSystraceHook();
    return static_cast<jboolean>(true);
}

JNI_METHOD_DECL(void, enableSystraceNative)
(JNIEnv *env, jclass type) {
    enableSystrace();
}

