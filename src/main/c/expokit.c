#include <uk_ac_ed_inf_expokit_ExpoKitC.h>

JNIEXPORT jint JNICALL Java_uk_ac_ed_inf_expokit_ExpoKitC_hello(
    JNIEnv *env, jobject obj, jint n) {
    return n+1;
}
