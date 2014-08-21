/*
 *  Tests for Native Scala Exponents
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
package uk.ac.ed.inf.expokit.test

import org.scalatest.{FlatSpec, Matchers}
import org.scalactic.TolerantNumerics

import java.lang.Math.E
import uk.ac.ed.inf.expokit

abstract class Tester extends Matchers {
  def test(f: Fix): Unit
}

abstract class MatrixTester extends Tester {
  def test(f: Fix) {
    val precision = 1e-3
    implicit val doubleEquality =
      TolerantNumerics.tolerantDoubleEquality(precision)

    val R = exp(f.m, f.t, f.H)
    for(i <- 0 until f.m*f.m)
      R(i) should equal (f.R(i))
  }
  def exp(m: Int, t: Double, H: Array[Double]): Array[Double]
}

abstract class VectorTester extends Tester {
  implicit class DotProduct(A: Array[Double]) {
    def dot(x: Array[Double]) = {
      val y = Array.fill(x.size)(0.0)
      for (i <- 0 until x.size)
        for (j <- 0 until x.size)
          y(i) += A(i*x.size + j)*x(j)
      y
    }
  }
  def test(f: Fix) {
    val precision = 1e-3
    implicit val doubleEquality =
      TolerantNumerics.tolerantDoubleEquality(precision)

    val w = exp(f.m, f.t, f.H, f.u)
    val ans = f.R.dot(f.u)
    for (i <- 0 until f.m)
      w(i) should equal (ans(i))
  }
  def exp(m: Int, t: Double, H: Array[Double], v: Array[Double]): Array[Double]
}

class PadeTester extends MatrixTester {
  def exp(m: Int, t: Double, H: Array[Double]): Array[Double] = {
    val ideg = 6
    val R = Array.fill(m*m)(0.0)
    expokit.dgpadm(ideg, m, t, H, R) should equal (0)
    R
  }
}

class ChebyshevTester extends VectorTester {
  def exp(m: Int, t: Double, H: Array[Double], v: Array[Double]): Array[Double] = {
    val y = v.clone
    expokit.dgchbv(m, t, H, y) should equal (0)
    y
  }
}

class KrylovTester extends VectorTester {
  def exp(m: Int, t: Double, H: Array[Double], v: Array[Double]): Array[Double] = {
    val tol = 0.0
    val anorm = 3.2556

    val w = Array.fill(m)(0.0)
    expokit.dgexpv(m, m-1, t, H, v, w, tol, anorm)
    w
  }
}

class PhiVTester extends VectorTester {
  def exp(m: Int, t: Double, H: Array[Double], v: Array[Double]): Array[Double] = {
    val tol = 0.0
    val norm = 3.2556

    val u = Array.fill(m)(0.0)
    val w = Array.fill(m)(0.0)
    expokit.dgphiv(m, m-1, t, H, u, v, w, tol, norm) should be (0)
    w
  }
}

class PhiUTester extends VectorTester {
  def exp(m: Int, t: Double, H: Array[Double], v: Array[Double]): Array[Double] = {
    val tol = 0.0
    val norm = 3.2556

    val u = Array.fill(m)(0.0)
    val w = Array.fill(m)(0.0)
    expokit.dgphiv(m, m-1, t, H, v, u, w, tol, norm) should be (0)
    w
  }
}

class PhiUVTester extends VectorTester {
  def exp(m: Int, t: Double, H: Array[Double], v: Array[Double]): Array[Double] = {
    val tol = 0.0
    val norm = 3.2556

    val w = Array.fill(m)(0.0)
    expokit.dgphiv(m, m-1, t, H, v, v, w, tol, norm) should be (0)
    w
  }
}


case class Fix (
  val m: Int,
  val t: Double,
  val H: Array[Double],
  val u: Array[Double],
  val R: Array[Double]
)

class ExpoKitTest extends FlatSpec with Matchers {

  val fixtures = Seq(
    Fix(m = 5, t = 2.0,
      H = Array(
        -0.2265e1,  0.3401,  0.1605,  0.3114,  0.6003,
        0.3401, -0.2390e1,  0.8465,  0.3564,  0.8367,
        0.1605,  0.8465, -0.2290e1,  0.5922,  0.5924,
        0.3114,  0.3564,  0.5922, -0.1982e1,  0.3205,
        0.6003,  0.8367,  0.5924,  0.3205, -0.1648e1
      ),
      u = Array(1.0, 0.0, 0.0, 0.0),
      R = Array(
        0.0999,  0.1385,  0.1304,  0.1082,  0.1775,
        0.1385,  0.2125,  0.2014,  0.1626,  0.2648,
        0.1304,  0.2014,  0.1953,  0.1582,  0.2502,
        0.1082,  0.1626,  0.1582,  0.1388,  0.2020,
        0.1775,  0.2648,  0.2502,  0.2020,  0.3404
      )
    ),
    Fix(m = 5, t = 2.0,
      H = Array(
        -0.2265e1,  0.3401,  0.1605,  0.3114,  0.6003,
        -0.3401, -0.2390e1,  0.8465,  0.3564,  0.8367,
        -0.1605, -0.8465, -0.2290e1,  0.5922,  0.5924,
        -0.3114, -0.3564,  -0.5922, -0.1982e1,  0.3205,
        -0.6003, -0.8367,  -0.5924,  -0.3205, -0.1648e1
      ),
      u = Array(1.0, 0.0, 0.0, 0.0, 0.0),
      R = Array(
        2.2397e-03,  -4.1927e-03,  -1.0175e-02,  -4.8849e-03,   1.3762e-02,
        -1.0071e-02,  -5.7011e-03,  -6.4634e-03,  -6.5654e-03,   5.4488e-03,
        2.2829e-03,  -6.4837e-03,  -3.7499e-03,  -1.4170e-05,  -6.6526e-03,
        -2.4191e-03,   5.6419e-03,  -1.0366e-02,   7.7542e-03,  -5.9885e-03,
        2.1813e-04,   2.2456e-03,  -3.6099e-03,  -2.1314e-02,  -3.1030e-04
      )
    ),
    Fix(m = 2, t = 1.0,
      H = Array(
        0.5, 0.5,
        0.5, 0.5
      ),
      u = Array(1.0, 1.0),
      R = Array(
        1.85914, 0.85914,
        0.85914, 1.85914
      )
    )
  )

  def prmatrix(H: Array[Double], m: Int) {
    import java.lang.Math.min
    for (i <- 0 until m) {
      print("  ")
      for (j <- 0 until m) {
        val s = H(i*m+j).toString
        val js = s.substring(0, min(s.size, 10))
        print(js)
        for (_ <- 0 until (12 -js.size)) print(" ")
      }
      print("\n")
    }
  }

  val pade = new PadeTester
  for (i <- 0 until fixtures.size) {
    "dgpadm" should s"compute exp(t*H) with Pade approximation ($i)" in {
      pade.test(fixtures(i))
    }
  }

  val cheby = new ChebyshevTester
  for (i <- 0 until fixtures.size) {
    "dgchbv" should s"compute exp(t*H)y with Chebyshev approximation ($i)" in {
      cheby.test(fixtures(i))
    }
  }

  val krylov = new KrylovTester
  for (i <- 0 until fixtures.size) {
    "dgexpv" should s"compute exp(t*H)y with Krylov subspace iteration ($i)" in {
      krylov.test(fixtures(i))
    }
  }

  val phiv = new PhiVTester
  for (i <- 0 until fixtures.size) {
    "dgphiv" should s"compute exp(t*H)v + t*phi(t*H)u (u=0 $i)" in {
      phiv.test(fixtures(i))
    }
  }

/*
  val phiu = new PhiUTester
  for (i <- 0 until fixtures.size) {
    "dgphiv" should s"compute exp(t*H)v + t*phi(t*H)u (uv=0 $i)" in {
      phiv.test(fixtures(i))
    }
  }

  val phiuv = new PhiUVTester
  for (i <- 0 until fixtures.size) {
    "dgphiv" should s"compute exp(t*H)v + t*phi(t*H)u ($i)" in {
      phiv.test(fixtures(i))
    }
  }
 */
}
