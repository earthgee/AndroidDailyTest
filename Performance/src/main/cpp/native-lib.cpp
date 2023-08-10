#include <jni.h>
#include <string>
#include "fbjni/fbjni.h"
#include "dlopen.h"
#include "allocTracker.h"

jint JNICALL JNI_OnLoad(JavaVM *vm, void *) {

    return facebook::jni::initialize(vm, [] {
        ndk_init(facebook::jni::Environment::current());
        hookFunc();
    });

}
