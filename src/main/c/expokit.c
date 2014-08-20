#include <stdlib.h>
#include <string.h>
#include "expokit.h"
#include <uk_ac_ed_inf_expokit_ExpoKitC.h>

// from netlib-java netlib-jni.c
inline void check_memory(JNIEnv *env, void *arg) {
    if (arg != NULL) {
	return;
    }
    /*
     * WARNING: Memory leak
     *
     * This doesn't clean up successful allocations prior to throwing this exception.
     * However, it's a pretty dire situation to be anyway and the client code is not
     * expected to recover.
     */
    (*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/OutOfMemoryError"),
		     "Out of memory transferring array to native code in F2J JNI");
}

/*
 * Calculage exp(t*H)
 *
 * ideg is the degree of the Pade polynomial (6 is usually sufficient)
 *
 * m is the order of the matrix H
 *
 * t is t
 *
 * H is the input matrix
 *
 * R is the result
 */
JNIEXPORT jint JNICALL Java_uk_ac_ed_inf_expokit_ExpoKitC_dgpadm(
    JNIEnv *env, jobject calling_obj,
    jint ideg, jint m, jdouble t, jdoubleArray H, jdoubleArray R)
{
    double *jni_H = NULL; // input array
    double *jni_R = NULL;
    double *wsp = NULL;
    int *ipiv = NULL;
    int iexph = 0, ns = 0, iflag = 0;
    int lwsp = 4*m*m+ideg+1;

    check_memory(env, H);
    check_memory(env, R);

    // work space
    wsp = (double *)malloc(lwsp*sizeof(double));
    check_memory(env, wsp);

    // more work space
    ipiv = (int *)malloc(m*sizeof(int));
    check_memory(env, wsp);

    jni_H = (*env)->GetPrimitiveArrayCritical(env, H, JNI_FALSE);
    check_memory(env, jni_H);

    DGPADM(ideg, m, t, jni_H, m, wsp, lwsp, ipiv, &iexph, &ns, &iflag);

    (*env)->ReleasePrimitiveArrayCritical(env, H, jni_H, 0);

    jni_R = (*env)->GetPrimitiveArrayCritical(env, R, JNI_FALSE);
    check_memory(env, jni_R);

    // the result is at offset iexph into the workspace. iexph is
    // from fortran-land where idexes start at 1.
    memmove(jni_R, wsp+iexph-1, m*m*sizeof(jdouble));

    (*env)->ReleasePrimitiveArrayCritical(env, R, jni_R, 0);

    free(ipiv);
    free(wsp);

    return iflag;
}

/*
 * Calculate exp(t*H)y
 *
 * m is the order of H (and length of y)
 */
JNIEXPORT jint JNICALL Java_uk_ac_ed_inf_expokit_ExpoKitC_dgchbv(
    JNIEnv *env, jobject calling_obj, 
    jint m, jdouble t, jdoubleArray H, jdoubleArray y)
{
    double *jni_H = NULL; // input array
    double *jni_y = NULL;
    double *wsp = NULL;
    int *iwsp = NULL;
    int iflag = 0;

    check_memory(env, H);
    check_memory(env, y);

    // work space, this is actually of size m*(m+2)*sizeof(complex 16)
    wsp = (double *)malloc(2*m*(m+2)*sizeof(double));
    check_memory(env, wsp);

    // more work space
    iwsp = (int *)malloc(m*sizeof(int));
    check_memory(env, iwsp);

    jni_H = (*env)->GetPrimitiveArrayCritical(env, H, JNI_FALSE);
    check_memory(env, jni_H);

    jni_y = (*env)->GetPrimitiveArrayCritical(env, y, JNI_FALSE);
    check_memory(env, jni_y);

    DGCHBV(m, t, jni_H, m, jni_y, wsp, iwsp, &iflag);

    (*env)->ReleasePrimitiveArrayCritical(env, y, jni_y, 0);
    (*env)->ReleasePrimitiveArrayCritical(env, H, jni_H, 0);

    return iflag;
}
