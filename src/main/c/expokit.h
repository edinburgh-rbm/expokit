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

#endif /* EXPOKIT_H */
