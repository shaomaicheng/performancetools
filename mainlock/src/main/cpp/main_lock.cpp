#include <jni.h>
#include <android/log.h>
#include "dlopen.h"
//
// Created by 程磊 on 2024/7/18.
//

extern "C"
JNIEXPORT void JNICALL
Java_lei_cheng_performancetools_mainlock_MainLockManager_00024Companion_native_1init(JNIEnv *env,jobject thiz) {
    ndk_init(env);
}