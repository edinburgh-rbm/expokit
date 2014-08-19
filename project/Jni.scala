import sbt._
import Keys._

object JniKeys {
  val javahName = settingKey[String](
    "The name of the javah command for generating JNI headers")
  val javahPath = settingKey[String](
    "The path to the javah executable")
  val jniClasses = settingKey[Seq[String]](
    "Fully qualified names of classes with native methods for which " +
      "JNI headers are to be generated")
  val javah = taskKey[Seq[File]](
    "Produce C headers from Java classes with native methods")
}

object JniBuild {
  import JniKeys._
  import NativeKeys._

  private val jdkHome = file(System.getProperty("java.home")) / ".."
  private val jdkInclude = jdkHome / "include"
  private val jdkOsInclude = jdkInclude / System.getProperty("os.name").toLowerCase
  lazy val jniSettings = Seq(
    javahName := "javah",
    javahPath <<= (javaHome, javahName) apply { (home, name) =>
      home map ( h => (h / "bin" / name).absolutePath ) getOrElse name
    },
    jniClasses := Seq.empty,
    cIncludes ++= Seq(jdkInclude.toString, jdkOsInclude.toString),
    libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value,
    javah in Compile := {
      val log = streams.value.log

      val classPath =
        (internalDependencyClasspath in Compile).value.map(_.data) ++
        (externalDependencyClasspath in Compile).value.map(_.data) ++
        Seq((classDirectory in Compile).value.toString)

      val javahCommandLine = Seq(
        javahPath.value,
        "-d", (resourceManaged in Compile).value.toString,
        "-cp", classPath.mkString(":")
      ) ++ jniClasses.value

      log.info(javahCommandLine mkString " ")
      val exitCode = Process(javahCommandLine) ! log
      if (exitCode != 0) {
        sys.error("javah exited with " + exitCode)
      }

      jniClasses.value map { s =>
        file(((resourceManaged in Compile).value / (s.replace(".", "_") + ".h")).toString)
      }
    }
  )
}
