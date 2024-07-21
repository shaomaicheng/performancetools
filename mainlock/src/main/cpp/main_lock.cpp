#include <jni.h>
#include <android/log.h>
#include "dlopen.h"
//
// Created by 程磊 on 2024/7/18.
//

extern "C"
JNIEXPORT void JNICALL
Java_lei_cheng_performancetools_mainlock_MainLockManager_00024Companion_native_1init(JNIEnv *env,
                                                                                     jobject thiz) {
    ndk_init(env);
}


const char *getContentedMonitorMethod = "_ZN3art7Monitor19GetContendedMonitorEPNS_6ThreadE";
const char *getLockOwnerThreadId = "_ZN3art7Monitor20GetLockOwnerThreadIdENS_6ObjPtrINS_6mirror6ObjectEEE";
const char *getTid = "_ZN3art6GetTidEv";

extern "C"
JNIEXPORT jint JNICALL
Java_lei_cheng_performancetools_mainlock_handler_MainLockBlockHandler_queryCompeteThreadId(
        JNIEnv *env, jobject thiz, jlong thread_id) {
    // 1. 获取主线程想要竞争的monitor：Monitor::GetContendedMonitor
    // * 2. 获取这个monitor被哪个线程持有：Monitor::GetLockOwnerThreadId
    void *so = ndk_dlopen("libart.so", RTLD_NOLOAD);
    if (so == nullptr) {
        return NULL;
    }
    int result_id = 0;
    void *getContentedMonitorMethod_orig = ndk_dlsym(so, getContentedMonitorMethod);
    void *getLockOwnerThreadId_orig = ndk_dlsym(so, getLockOwnerThreadId);
    if (getContentedMonitorMethod_orig != nullptr && getLockOwnerThreadId_orig != nullptr) {
        int monitorThreadId = ((int (*)(long)) getContentedMonitorMethod_orig)(thread_id);
        if (monitorThreadId != 0) {
            result_id = ((int (*)(int)) (getLockOwnerThreadId_orig))(monitorThreadId);
        }
    }
    return result_id;
}


/**
 * Thread内存布局：peer-> tls32_ -> state_and_flags，suspend_count, thin_lock_thread_id, tid
 * thin_lock_thread_id  为 GetLockOwnerThreadId返回的id
 */
extern "C"
JNIEXPORT jint JNICALL
Java_lei_cheng_performancetools_mainlock_handler_MainLockBlockHandler_threadIdByPtr(JNIEnv *env,
                                                                                    jobject thiz,
                                                                                    jlong ptr) {
    int* ptrPoint = reinterpret_cast<int *>(ptr);
    ptrPoint += 2;
    return *ptrPoint;
}





/**
 * 纯编写边测试用的，懒得删，别看
 */

extern "C"
JNIEXPORT void JNICALL
Java_lei_cheng_performancetools_mainlock_MainLockManager_00024Companion_test1(JNIEnv *env,
                                                                              jobject thiz,
                                                                              jlong thread,
                                                                              jlong otherthread) {
    void *so = ndk_dlopen("libart.so", RTLD_NOLOAD);
    __android_log_print(ANDROID_LOG_ERROR, "chenglei", "thread：%ld,other:%ld", thread, otherthread);
    if (so == nullptr) {
        return;
    }
    void *getTid_orig = ndk_dlsym(so, getTid);
    if (getTid_orig != nullptr) {
        __android_log_print(ANDROID_LOG_ERROR, "chenglei", "查找成功！%ld", thread);
        int tid = ((int (*)(void *)) getTid_orig)((void *) thread);
        int *tidp = &tid;
        tidp -= 1;
        int *pInt = reinterpret_cast<int *>(thread);
        pInt += 2;
        __android_log_print(ANDROID_LOG_ERROR, "chenglei",
                            "主线程tid：%ld,%ld,主线程偏移算出来的tid：%d", tid, *tidp, *pInt);

        void *getContentedMonitorMethod_orig = ndk_dlsym(so, getContentedMonitorMethod);
        int monitorThreadId = ((int (*)(long)) getContentedMonitorMethod_orig)(otherthread);
        __android_log_print(ANDROID_LOG_ERROR, "chenglei", "monit id：%ld", monitorThreadId);

        void *getLockOwnerThreadId_orig = ndk_dlsym(so, getLockOwnerThreadId);
        if (monitorThreadId != 0) {
            int result_id = ((int (*)(int)) (getLockOwnerThreadId_orig))(monitorThreadId);
            __android_log_print(ANDROID_LOG_ERROR, "chenglei", "monit 持有线程id：%d", result_id);

        }
    }
}