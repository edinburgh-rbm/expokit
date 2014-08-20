/*
 *  ExpoKit Shared Library Scala Language bindings
 *  Copyright (C) 2014 University of Edinburgh
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.ed.inf

import com.github.fommil.jni.{JniLoader, JniNamer}

/**
  *----------------------------------------------------------------------|
  *     Roger B. Sidje (rbs@maths.uq.edu.au)
  *     EXPOKIT: Software Package for Computing Matrix Exponentials.
  *     ACM - Transactions On Mathematical Software, 24(1):130-156, 1998
  *----------------------------------------------------------------------|
  */
package object expokit {
  private lazy val native = {
    JniLoader.load(JniNamer.getJniName("expokit"))
    new expokit.ExpoKitC
  }

  /** DGPADM
    *-----Purpose----------------------------------------------------------|
    *
    *     Computes exp(t*H), the matrix exponential of a general matrix in
    *     full, using the irreducible rational Pade approximation to the
    *     exponential function exp(x) = r(x) = (+/-)( I + 2*(q(x)/p(x)) ),
    *     combined with scaling-and-squaring.
    *
    *-----Arguments--------------------------------------------------------|
    *
    * @param ideg (input) the degre of the diagonal Pade to be used.
    *             a value of 6 is generally satisfactory.
    *
    * @param m (input) order of H.
    *
    * @param H(ldh,m) (input) argument matrix.
    *
    * @param t (input) time-scale (can be < 0).
    *
    * @param R result of the computation.
    */
  @inline final def dgpadm(
    ideg: Int, m: Int, t: Double, H: Array[Double], R: Array[Double]
  ) = native.dgpadm(ideg, m, t, H, R)

  /** DHCHBV
    *
    *-----Purpose----------------------------------------------------------|
    *
    *---  DGCHBV computes y = exp(t*H)*y using the partial fraction
    *     expansion of the uniform rational Chebyshev approximation
    *     to exp(-x) of type (14,14). H is a General matrix.
    *     About 14-digit accuracy is expected if the matrix H is negative
    *     definite. The algorithm may behave poorly otherwise.
    *
    *-----Arguments--------------------------------------------------------|
    *
    * @param m (input) order of the matrix H
    *
    * @param t (input) time-scaling factor (can be < 0).
    *
    * @param H(m,m) (input) argument matrix.
    *
    * @param y(m) (input/output) on input the operand vector,
    *             on output the resulting vector exp(t*H)*y.
    */
  @inline final def dgchbv(
    m: Int, t: Double, H: Array[Double], y: Array[Double]
  ) = native.dgchbv(m, t, H, y)

  /** DGEXPV
    *-----Purpose----------------------------------------------------------|
    *
    *---  DGEXPV computes w = exp(t*A)*v - for a General matrix A.
    *
    *     It does not compute the matrix exponential in isolation but
    *     instead, it computes directly the action of the exponential
    *     operator on the operand vector. This way of doing so allows
    *     for addressing large sparse problems.
    *
    *     The method used is based on Krylov subspace projection
    *     techniques and the matrix under consideration interacts only
    *     via the external routine `matvec' performing the matrix-vector
    *     product (matrix-free method).
    *
    *-----Arguments--------------------------------------------------------|
    *
    * @param n (input) order of the principal matrix A.
    *
    * @param m (input) maximum size for the Krylov basis.
    *
    * @param t (input) time at wich the solution is needed (can be < 0).
    *
    * @param A (input) principal matrix A
    *
    * @param v(n) (input) given operand vector.
    *
    * @param w(n) (output) computed approximation of exp(t*A)*v.
    *
    * @param tol (input/output) the requested accuracy tolerance on w.
    *            If on input tol=0.0d0 or tol is too small (tol.le.eps)
    *            the internal value sqrt(eps) is used, and tol is set to
    *            sqrt(eps) on output (`eps' denotes the machine epsilon).
    *            (`Happy breakdown' is assumed if h(j+1,j) .le. anorm*tol)
    *            N.B. input only for the JVM!
    */
  @inline final def dgexpv(n: Int, m: Int, t: Double, A: Array[Double], v: Array[Double],
    w: Array[Double], tol: Double, anorm: Double
  ) = native.dgexpv(n, m, t, A, v, w, tol, anorm)

  /** DGPHIV
    *-----Purpose----------------------------------------------------------|
    *
    *---  DGPHIV computes w = exp(t*A)v + t*phi(tA)u which is the solution
    *     of the nonhomogeneous linear ODE problem w' = Aw + u, w(0) = v.
    *     phi(z) = (exp(z)-1)/z and A is a General matrix.
    *
    *     The method used is based on Krylov subspace projection
    *     techniques and the matrix under consideration interacts only
    *     via the external routine `matvec' performing the matrix-vector
    *     product (matrix-free method).
    *
    *-----Arguments--------------------------------------------------------|
    *
    * @param n (input) order of the principal matrix A.
    *
    * @param m (input) maximum size for the Krylov basis.
    *
    * @param t (input) time at wich the solution is needed (can be < 0).
    *
    * @param A (input) principal matrix A
    *
    * @param u(n) (input) operand vector with respect to the phi function
    *             (forcing term of the ODE problem).
    *
    * @param v(n) (input) operand vector with respect to the exp function
    *             (initial condition of the ODE problem).
    *
    * @param w(n) (output) computed approximation of exp(t*A)v + t*phi(tA)u
    *
    * @param tol (input/output) the requested accuracy tolerance on w.
    *            If on input tol=0.0d0 or tol is too small (tol.le.eps)
    *            the internal value sqrt(eps) is used, and tol is set to
    *            sqrt(eps) on output (`eps' denotes the machine epsilon).
    *            (`Happy breakdown' is assumed if h(j+1,j) .le. anorm*tol)
    *            N.B. input only for the JVM!
    *
    * @param anorm (input) an approximation of some norm of A.
    */
  @inline final def dgphiv(n: Int, m: Int, t: Double, A: Array[Double], u: Array[Double],
    v: Array[Double], w: Array[Double], tol: Double, anorm: Double
  ) = native.dgphiv(n, m, t, A, u, v, w, tol, anorm)
}
