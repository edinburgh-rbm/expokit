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

import uk.ac.ed.inf.expokit

class ExpoKitTest extends FlatSpec with Matchers {

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

  "dgpadm" should "compute exp(t*H)" in {
    val precision = 1e-3
    implicit val doubleEquality =
      TolerantNumerics.tolerantDoubleEquality(precision)

    val ideg = 6
    val m = 5
    val H = Array(
      -0.2265e1,  0.3401,  0.1605,  0.3114,  0.6003,
      0.3401, -0.2390e1,  0.8465,  0.3564,  0.8367,
      0.1605,  0.8465, -0.2290e1,  0.5922,  0.5924,
      0.3114,  0.3564,  0.5922, -0.1982e1,  0.3205,
      0.6003,  0.8367,  0.5924,  0.3205, -0.1648e1
    )

    val Expected = Array(
      0.0999,  0.1385,  0.1304,  0.1082,  0.1775,
      0.1385,  0.2125,  0.2014,  0.1626,  0.2648,
      0.1304,  0.2014,  0.1953,  0.1582,  0.2502,
      0.1082,  0.1626,  0.1582,  0.1388,  0.2020,
      0.1775,  0.2648,  0.2502,  0.2020,  0.3404
    )

    val R = Array.fill(m*m)(0.0)
    val t = 2.0
    expokit.dgpadm(ideg, m, t, H, R)

/*
    println(s"H = ")
    prmatrix(H, m)
    println("")
    println(s"R (supposedly exp(${t}H) =")
    prmatrix(R, m)
    println("")
    println(s"Expecting")
    prmatrix(Expected, m)
    println("")
 */

    for (i <- 0 until H.size) {
      R(i) should equal (Expected(i))
    }
  }
}
