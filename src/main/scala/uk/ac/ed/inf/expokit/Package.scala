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
}
