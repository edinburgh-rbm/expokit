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

import uk.ac.ed.inf.expokit.ExpoKit

class ExpoKitTest extends FlatSpec with Matchers {
  "an example" should "test things here" in {

    ExpoKit.hello(1) should be (2)
  }
}
