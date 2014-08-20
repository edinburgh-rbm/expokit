#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "expokit.h"
#include <uk_ac_ed_inf_expokit_ExpoKitC.h>

#define MAX(a,b) (a > b ? a : b)

// from netlib-java netlib-jni.c
inline int check_memory(JNIEnv *env, void *arg) {
    if (arg != NULL) {
	return 0;
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
    return -1;
}

/* #define CHECK_MEMORY(m) { if (check_memory(env, m)) return -1; } */
#define CHECK_MEMORY(m) check_memory(env, m)
#define RELEASE_MEMORY(m, jni_m) { \
        if (m != NULL) (*env)->ReleasePrimitiveArrayCritical(env, m, jni_m, 0); \
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

    CHECK_MEMORY(H);
    CHECK_MEMORY(R);

    // work space
    wsp = (double *)malloc(lwsp*sizeof(double));
    CHECK_MEMORY(wsp);

    // more work space
    ipiv = (int *)malloc(m*sizeof(int));
    CHECK_MEMORY(ipiv);

    jni_H = (*env)->GetPrimitiveArrayCritical(env, H, JNI_FALSE);
    CHECK_MEMORY(jni_H);

    DGPADM(ideg, m, t, jni_H, m, wsp, lwsp, ipiv, &iexph, &ns, &iflag);

    RELEASE_MEMORY(H, jni_H);

    jni_R = (*env)->GetPrimitiveArrayCritical(env, R, JNI_FALSE);
    CHECK_MEMORY(jni_R);

    // the result is at offset iexph into the workspace. iexph is
    // from fortran-land where idexes start at 1.
    memmove(jni_R, wsp+iexph-1, m*m*sizeof(jdouble));

    RELEASE_MEMORY(R, jni_R);

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

    CHECK_MEMORY(H);
    CHECK_MEMORY(y);

    // work space, this is actually of size m*(m+2)*sizeof(complex 16)
    wsp = (double *)malloc(2*m*(m+2)*sizeof(double));
    CHECK_MEMORY(wsp);

    // more work space
    iwsp = (int *)malloc(m*sizeof(int));
    CHECK_MEMORY(iwsp);

    jni_H = (*env)->GetPrimitiveArrayCritical(env, H, JNI_FALSE);
    CHECK_MEMORY(jni_H);

    jni_y = (*env)->GetPrimitiveArrayCritical(env, y, JNI_FALSE);
    CHECK_MEMORY(jni_y);

    DGCHBV(m, t, jni_H, m, jni_y, wsp, iwsp, &iflag);

    RELEASE_MEMORY(y, jni_y);
    RELEASE_MEMORY(H, jni_H);

    return iflag;
}


/* 
 * Allocates memory on the heap here. This is used by the matrix
 * multiplication routines DGCOOV, DGCRSV and DGCCSV. We have to
 * initialise it in our wrappers that call these functions via
 * DGEXPV and friends.
 */
RMAT_DEF RMAT; 

static inline int copy_to_rmat(JNIEnv *env, int n, jdoubleArray A)
{
    double *jni_A = NULL; // input matrix
    register int i,j;

    /* serious brain damage here. need to copy the input matrix into
     * the fortran common global variable area. this is very not thread
     * safe! */
    if (n*n > NZMAX) // not enough room on the common area
	return -1;

    RMAT.n = n*n;
    RMAT.nz = n*n;
    jni_A = (*env)->GetPrimitiveArrayCritical(env, A, JNI_FALSE);
    CHECK_MEMORY(jni_A);
    memcpy(RMAT.a, jni_A, n*n*sizeof(double));
    RELEASE_MEMORY(A, jni_A);

    /* this is a dense matrix so we need to set all i and j */
    for(i = 0; i < n; i++) {
	for(j = 0; j < n; j++) {
            // fortran indices start at 1 and the matrix is transposed
	    RMAT.ja[j*n+i] = i+1;
	    RMAT.ia[j*n+i] = j+1;
	    //printf("%d %d %g\n", RMAT.ia[j*n+i], RMAT.ja[j*n+i], RMAT.a[j*n+i]);
	}
    }
    return 0;
}

JNIEXPORT jint JNICALL Java_uk_ac_ed_inf_expokit_ExpoKitC_dgexpv(
    JNIEnv *env, jobject calling_object,
    jint n, jint m, jdouble t, jdoubleArray A, jdoubleArray v, jdoubleArray w,
    jdouble tol, jdouble anorm)
{
    double *jni_v = NULL; // input operand
    double *jni_w = NULL; // output vector
    double *wsp = NULL; // workspace
    int ideg = 6; 
    int lwsp = MAX(12, n*(m+2)+5*(m+2)*(m+2)+ideg+1); // ugh!
    int *iwsp = NULL; // more workspace
    int liwsp = MAX(8, m+2); // ugh! ugh!
    int itrace = 0, iflag = 0;
    int i, j;

    if (copy_to_rmat(env, n, A)) return -1;

    wsp = (double *)malloc(lwsp*sizeof(double));
    CHECK_MEMORY(wsp);

    iwsp = (int *)malloc(liwsp*sizeof(int));
    CHECK_MEMORY(iwsp);

    jni_v = (*env)->GetPrimitiveArrayCritical(env, v, JNI_FALSE);
    CHECK_MEMORY(jni_v);

    jni_w = (*env)->GetPrimitiveArrayCritical(env, w, JNI_FALSE);
    CHECK_MEMORY(jni_w);

    DGEXPV(n, m, t, jni_v, jni_w, &tol, anorm,
	   wsp, lwsp, iwsp, liwsp, dgcoov_, itrace, &iflag);

    RELEASE_MEMORY(w, jni_w);
    RELEASE_MEMORY(v, jni_v);

    free(iwsp);
    free(wsp);

    return iflag;
}

JNIEXPORT jint JNICALL Java_uk_ac_ed_inf_expokit_ExpoKitC_dgphiv(
    JNIEnv *env, jobject calling_object,
    jint n, jint m, jdouble t, jdoubleArray A, jdoubleArray u, jdoubleArray v,
    jdoubleArray w, jdouble tol, jdouble anorm)
{
    if (copy_to_rmat(env, n, A)) return -1;
    double *jni_u = NULL; // forcing vector
    double *jni_v = NULL; // input operand
    double *jni_w = NULL; // output vector
    double *wsp = NULL; // workspace
    int ideg = 6; 
    int lwsp = MAX(9, n*(m+3)+5*(m+3)*(m+3)+ideg+1); // ugh!
    int *iwsp = NULL; // more workspace
    int liwsp = MAX(8, m+3); // ugh! ugh!
    int itrace = 0, iflag = 0;
    int i, j;

    if (copy_to_rmat(env, n, A)) return -1;

    wsp = (double *)malloc(lwsp*sizeof(double));
    CHECK_MEMORY(wsp);

    iwsp = (int *)malloc(liwsp*sizeof(int));
    CHECK_MEMORY(iwsp);

    jni_u = (*env)->GetPrimitiveArrayCritical(env, u, JNI_FALSE);
    CHECK_MEMORY(jni_u);

    jni_v = (*env)->GetPrimitiveArrayCritical(env, v, JNI_FALSE);
    CHECK_MEMORY(jni_v);

    jni_w = (*env)->GetPrimitiveArrayCritical(env, w, JNI_FALSE);
    CHECK_MEMORY(jni_w);

    DGPHIV(n, m, t, jni_u, jni_v, jni_w, &tol, anorm,
	   wsp, lwsp, iwsp, liwsp, dgcoov_, itrace, &iflag);

    RELEASE_MEMORY(w, jni_w);
    RELEASE_MEMORY(v, jni_v);
    RELEASE_MEMORY(u, jni_u);

    free(iwsp);
    free(wsp);

    return iflag;
}

