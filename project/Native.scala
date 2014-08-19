import sbt._
import Keys._

import scala.collection.mutable
import com.github.fommil.jni.JniNamer

object NativeKeys {
  val cc = settingKey[String](
    "The name of the C compiler")
  val cFlags = settingKey[Seq[String]](
    "The flags for the C compiler")
  val cIncludes = settingKey[Seq[String]](
    "Include directories for the C compiler")
  val cSources = settingKey[File](
    "The directory where to find the C source files")
  val f95 = settingKey[String](
    "The name of the Fortran 95 compiler")
  val fFlags = settingKey[Seq[String]](
    "The flags for the Fortran 95 compiler")
  val fIncludes = settingKey[Seq[String]](
    "Include directories for the Fortran 95 compiler")
  val fSources = settingKey[File](
    "The directory where to find the C source files")
  val ld = settingKey[String](
    "The name of the linker")
  val ldFlags = settingKey[Seq[String]](
    "The linker flags")
  val ldLibraryPath = settingKey[Seq[String]](
    "The linker search path")
  val ldLibraries = settingKey[Seq[String]](
    "The libraries to link against")
  val sharedLibrary = settingKey[String](
    "The shared library to produce")
  val native = taskKey[Seq[File]](
    "Compile native code")
}

object NativeBuild {
  import NativeKeys._

  lazy val nativeSettings = Seq(
    cc := "gcc",
    cFlags := Seq("-O3", "-c", "-fPIC"),
    cSources := new File("src/main/c"),
    cIncludes := Seq((resourceManaged in Compile).value.toString),
    f95 := "f95",
    fFlags := Seq("-O3", "-c", "-fPIC"),
    fSources := new File("src/main/fortran"),
    ld := "ld",
    ldFlags := Seq("-Bshareable", "-Bdynamic", "-E"),
    ldLibraryPath := Seq.empty,
    ldLibraries := Seq.empty,
    native in Compile := {
      val log = streams.value.log

      val objects = mutable.ArrayBuffer.empty[String]

      def obj(f: File): File = {
        val dir = (resourceManaged in Compile).value
        val name =  f.getName
        dir / (name.substring(0, name.size-1) + "o")
      }

      for (source <- cSources.value.listFiles.filter(_.getName.endsWith(".c"))) {
        val o = obj(source).toString
        objects += o

        val cCommandLine = Seq(
          cc.value, "-o", o
        ) ++ cFlags.value ++ cIncludes.value.map(d => "-I" + d).toSeq ++
        Seq(source.toString)

        log.info(cCommandLine mkString " ")
        val exitCode = Process(cCommandLine) ! log
        if (exitCode != 0) {
          sys.error("c compiler exited with " + exitCode)
        }
      }

      for (source <- fSources.value.listFiles.filter(_.getName.endsWith(".f"))) {
        val o = obj(source).toString
        objects += o

        val fCommandLine = Seq(
          f95.value, "-o", o
        ) ++ fFlags.value ++ Seq(source.toString)

        log.info(fCommandLine mkString " ")
        val exitCode = Process(fCommandLine) ! log
        if (exitCode != 0) {
          sys.error("fortran compiler exited with " + exitCode)
        }
      }

      val soName =
        (resourceManaged in Compile).value /
        JniNamer.getJniName((sharedLibrary in Compile).value)

      val ldCommandLine = Seq(
        ld.value, "-o", soName.toString
      ) ++ ldFlags.value ++ ldLibraryPath.value.map(d => "-L" + d).toSeq ++
      objects.toSeq ++ ldLibraries.value.map(d => "-l" + d).toSeq

      log.info(ldCommandLine mkString " ")
      val exitCode = Process(ldCommandLine) ! log
      if (exitCode != 0) {
        sys.error("linker exited with " + exitCode)
      }

      Seq(file(soName.toString))
    },
    resourceGenerators in Compile += task[Seq[File]] {
      val soName =
        (resourceManaged in Compile).value /
        JniNamer.getJniName((sharedLibrary in Compile).value)
      Seq(file(soName.toString))
    }
  )
}
