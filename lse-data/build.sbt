import AssemblyKeys._

import sbtbuildinfo.Plugin._

import com.typesafe.sbt.SbtNativePackager._

import NativePackagerKeys._

assemblySettings

buildInfoSettings

sourceGenerators in Compile <+= buildInfo

buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion, buildInfoBuildNumber)

buildInfoPackage := "org.ccfea.tickdata.conf"

name := "lse-data"

organization := "net.sourceforge.jasa"

version := "0.19-SNAPSHOT"

scalaVersion := "2.11.7"

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
  "JABM" at "http://jabm.sourceforge.net/mvn-repo/jabm"
)

libraryDependencies ++= Seq(
  "org.apache.hbase" % "hbase-client" % "1.1.2",
  "org.apache.hbase" % "hbase-common" % "1.1.2" excludeAll ExclusionRule(organization = "javax.servlet"),
  "org.apache.hbase" % "hbase-server" % "1.1.2" excludeAll ExclusionRule(organization = "org.mortbay.jetty"),
//  "org.apache.hadoop" % "hadoop-client" % "2.7.1",
//  "org.apache.hadoop" % "hadoop-common" % "2.7.1",
  "org.apache.thrift" % "libthrift" % "0.9.2",
  "net.sourceforge.jasa" % "jasa" % "1.2.6-SNAPSHOT",
//  "com.espertech" % "esper" % "4.11.0",
  "org.rogach" %% "scallop" % "0.9.5",
  "org.clapper" % "grizzled-slf4j_2.10" % "1.0.2",
  "org.slf4j" % "slf4j-log4j12" % "1.7.7",
  "org.apache.spark" %% "spark-core" % "1.5.2"
)

mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
  {
    case PathList("org", "apache", "jasper", xs @ _*)   => MergeStrategy.first
    case PathList("org", "apache", "commons", xs @ _*) => MergeStrategy.first
    case PathList("javax", "xml", xs @ _*)   => MergeStrategy.first
    case PathList("javax", "servlet", xs @ _*)         => MergeStrategy.first
    case PathList("net", "sf", "cglib", xs @ _*)         => MergeStrategy.first
    case PathList(ps @ _*) if ps.last endsWith ".html" => MergeStrategy.first
    case PathList(ps @ _*) if ps.last endsWith "pom.xml" => MergeStrategy.concat
    case PathList(ps @ _*) if ps.last endsWith "pom.properties" => MergeStrategy.concat
    case PathList(ps @ _*) if ps.last endsWith "spring.tooling" => MergeStrategy.first
    case PathList(ps @ _*) if ps.last endsWith "package-info.class" => MergeStrategy.first
    case PathList(ps @ _*) if ps.last equals "Present.class" => MergeStrategy.first
    case PathList(ps @ _*) if ps contains "minlog" => MergeStrategy.first
    case "log4j.xml" => MergeStrategy.first
    case "pom.xml" => MergeStrategy.concat
    case x => old(x)
  }
}

