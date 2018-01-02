import AssemblyKeys._

import com.typesafe.sbt.SbtNativePackager._

import NativePackagerKeys._

assemblySettings

lazy val root = (project in file(".")).
  enablePlugins(BuildInfoPlugin).
  settings(
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoPackage := "org.ccfea.tickdata.conf"
  )

buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion, buildInfoBuildNumber)

name := "lse-data"

organization := "net.sourceforge.jasa"

version := "0.21-SNAPSHOT"

scalaVersion := "2.12.4"

sbtVersion := "0.13.16"

packSettings

packMain := Map(  "replay-orders"         -> "org.ccfea.tickdata.ReplayOrders",
                  "order-replay-service"  -> "org.ccfea.tickdata.OrderReplayService",
                  "import-data"           -> "org.ccfea.tickdata.ImportData",
                  "orderbook-snapshot"    -> "org.ccfea.tickdata.OrderBookSnapshot"
)

publishMavenStyle := true

publishTo := Some(Resolver.file("file",  new File(Path.userHome.absolutePath+"/.m2/repository")))

//scalaSource in Compile := file("src/")
javaSource in Compile := baseDirectory.value / "src/main/thrift/gen-java"

//resolvers := ("Local Maven Repository" at "file:///" + Path.userHome.absolutePath + "/.m2/repository") +: resolvers.value

resolvers ++= Seq(
  "Local Maven Repository" at "file:///" + Path.userHome.absolutePath + "/.m2/repository",
  "Apache HBase" at "http://repository.apache.org/content/repositories/releases",
  "JASA" at "http://jasa.sourceforge.net/mvn-repo/jasa",
  "JABM" at "http://jabm.sourceforge.net/mvn-repo/jabm",
  "Maven Central" at "http://repo1.maven.org/maven2"
)

libraryDependencies ++= Seq(
  "org.apache.hbase" % "hbase-client" % "1.2.4",
  "org.apache.hbase" % "hbase-common" % "1.2.4" excludeAll ExclusionRule(organization = "javax.servlet"),
  "org.apache.hbase" % "hbase-server" % "1.2.4" excludeAll ExclusionRule(organization = "org.mortbay.jetty"),
  "org.apache.hadoop" % "hadoop-client" % "2.7.1",
  "org.apache.hadoop" % "hadoop-common" % "2.7.1",
  "org.apache.thrift" % "libthrift" % "0.9.2",
  "net.sourceforge.jasa" % "jasa" % "1.2.9-SNAPSHOT",
//  "com.espertech" % "esper" % "4.11.0",
  "org.rogach" %% "scallop" % "2.1.1",
  "org.clapper" %% "grizzled-slf4j" % "1.3.0",
  //  "org.apache.spark" %% "spark-core" % "1.5.2",
  "org.slf4j" % "slf4j-log4j12" % "1.7.7"
)

unmanagedClasspath in Test += baseDirectory.value / "etc"

unmanagedClasspath in (Compile, runMain) += baseDirectory.value / "etc"

scalacOptions in (Compile, doc) ++= {
      Seq("-skip-packages", "org.ccfea.tickdata.thrift") ++
          (if (scalaBinaryVersion.value == "2.12") Seq("-no-java-comments") else Nil)
      }

//sources in (Compile, doc) ~= (_ filter (_.getName endsWith ".scala"))