#ifndef _AQUILA_DTW_H_INCLUDED_
#define _AQUILA_DTW_H_INCLUDED_
#include <jni.h>

extern "C" {
    JNIEXPORT jdouble Java_de_fhaachen_navi_gesture_solver_aquila_Dtw_getDistance(JNIEnv *env, jobject obj, jobjectArray fromAryJ, jobjectArray toAryJ);
    JNIEXPORT jstring Java_de_fhaachen_navi_gesture_solver_aquila_Dtw_sayHello(JNIEnv *env, jobject obj);
}

#endif
