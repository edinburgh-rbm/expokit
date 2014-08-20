#ifndef EXPOKIT_H
#define EXPOKIT_H

#include "cfortran.h"

PROTOCCALLSFSUB11(DGPADM, dgpadm, \
		  INT, INT, DOUBLE, DOUBLEV, INT, DOUBLEV, INT, \
		  INTV, INTV, INTV, INTV)
#define DGPADM(ideg, m, t, H, ldh, wsp, lwsp, ipiv, iexph, ns, iflag)	\
    CCALLSFSUB11(DGPADM, dgpadm, \
                 INT, INT, DOUBLE, DOUBLEV, INT, DOUBLEV, INT, \
                 INTV, INTV, INTV, INTV, \
                 ideg, m, t, H, ldh, wsp, lwsp, ipiv, iexph, ns, iflag)

PROTOCCALLSFSUB8(DGCHBV, dgchbv, \
		 INT, DOUBLE, DOUBLEV, INT, DOUBLEV, DOUBLEV, INTV, INTV)
#define DGCHBV(m, t, H, ldh, y, wsp, iwsp, iflag) \
    CCALLSFSUB8(DGCHBV, dgchbv, \
		INT, DOUBLE, DOUBLEV, INT, DOUBLEV, DOUBLEV, INTV, INTV, \
		m, t, H, ldh, y, wsp, iwsp, iflag)

/*
 * DGEXPV does some *weird* stuff. It relies on a low-level matrix
 * multiplication routine that is passed common block memory from
 * the calling routine. This strikes me as *extremely* brittle, but
 * we'll do that here for now. So what this means is, it makes this
 * memory available to DGCOOV and friends who use it internally, and
 * we have to set it up and initialise it in our C wrappers.
 */
#define NZMAX 600000 // magic number in dgmatv.f
typedef struct {
    double a[NZMAX];
    int ia[NZMAX];
    int ja[NZMAX];
    int nz;
    int n;
} RMAT_DEF;
#define RMAT COMMON_BLOCK(RMAT, rmat)
COMMON_BLOCK_DEF(RMAT_DEF, RMAT);

PROTOCCALLSFSUB2(DGCOOV, dgcoov, DOUBLEV, DOUBLEV)
#define DGCOOV(x, y) CCALLSFSUB2(DGCOOV, dgcoov, DOUBLEV, DOUBLEV, x, y)

PROTOCCALLSFSUB14(DGEXPV, dgexpv, \
		  INT, INT, DOUBLE, DOUBLEV, DOUBLEV, DOUBLEV, DOUBLE, \
		  DOUBLEV, INT, INTV, INT, ROUTINE, INT, INTV)
#define DGEXPV(n, m, t, v, w, tol, anorm, \
	       wsp,lwsp, iwsp,liwsp, matvec, itrace,iflag) \
    CCALLSFSUB14(DGEXPV, dgexpv, \
		 INT, INT, DOUBLE, DOUBLEV, DOUBLEV, DOUBLEV, DOUBLE,	\
		 DOUBLEV, INT, INTV, INT, ROUTINE, INT, INTV,	\
		 n, m, t, v, w, tol, anorm, \
		 wsp,lwsp, iwsp,liwsp, matvec, itrace,iflag)

PROTOCCALLSFSUB15(DGPHIV, dgphiv, \
		  INT, INT, DOUBLE, DOUBLEV, DOUBLEV, DOUBLEV, DOUBLEV, DOUBLE, \
		  DOUBLEV, INT, INTV, INT, ROUTINE, INT, INTV)
#define DGPHIV(n, m, t, u, v, w, tol, anorm,		   \
	       wsp,lwsp, iwsp,liwsp, matvec, itrace,iflag) \
    CCALLSFSUB15(DGPHIV, dgphiv, \
		 INT, INT, DOUBLE, DOUBLEV, DOUBLEV, DOUBLEV, DOUBLEV, DOUBLE, \
		 DOUBLEV, INT, INTV, INT, ROUTINE, INT, INTV,	\
		 n, m, t, u, v, w, tol, anorm,			\
		 wsp,lwsp, iwsp,liwsp, matvec, itrace,iflag)

#endif /* EXPOKIT_H */
