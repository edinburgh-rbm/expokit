/*
 *  Native Exponentiation Methods for Scala
 *  Copyright (C) 2014 Weisse et al
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
import sbt._
import Keys._
import JniKeys._
import NativeKeys._

object ExpBuild extends Build {
  lazy val root = Project(
    id="root",
    base=file("."),
    settings=
      Defaults.defaultSettings ++ JniBuild.jniSettings ++ NativeBuild.nativeSettings ++
        Seq(
          name := "expokit",
          organization := "uk.ac.ed.inf",
          version := "0.1-SNAPSHOT",
          scalaVersion := "2.11.2",
//          resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
          libraryDependencies += "com.github.fommil" % "jniloader" % "1.1",
          libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.0" % "test",
          jniClasses := Seq("uk.ac.ed.inf.expokit.ExpoKitC"),
          cIncludes += "/usr/lib/jvm/java-7-openjdk-amd64/include/",
          ldLibraries ++= Seq("lapack", "blas"),
          sharedLibrary := "expokit"
        )
  )
}
