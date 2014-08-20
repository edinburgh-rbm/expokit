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
package uk.ac.ed.inf.expokit

private class ExpoKitC {
  @native def dgpadm(ideg: Int, m: Int, t: Double, H: Array[Double], R: Array[Double]): Int
  @native def dgchbv(m: Int, t: Double, H: Array[Double], y: Array[Double]): Int
  @native def dgexpv(n: Int, m: Int, t: Double, A: Array[Double], v: Array[Double],
    w: Array[Double], tol: Double, anorm: Double): Int
  @native def dgphiv(n: Int, m: Int, t: Double, A: Array[Double], u: Array[Double],
    v: Array[Double], w: Array[Double], tol: Double, anorm: Double): Int
}
