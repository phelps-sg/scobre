import AssemblyKeys._

assemblySettings

net.virtualvoid.sbt.graph.Plugin.graphSettings

name := "lse-data"

version := "0.12"

scalaVersion := "2.11.2"

scalaSource in Compile := file("src/")

resolvers ++= Seq(
  "Apache HBase" at "http://repository.apache.org/content/repositories/releases",
  "JASA" at "http://jasa.sourceforge.net/mvn-repo/jasa",
  "JABM" at "http://jabm.sourceforge.net/mvn-repo/jabm"
)

libraryDependencies ++= Seq(
	"org.apache.hbase" % "hbase-client" % "0.98.5-hadoop2",
	"org.apache.hbase" % "hbase-common" % "0.98.5-hadoop2",
//	"org.apache.hadoop" % "hadoop-core" % "1.2.1"
  "org.apache.hadoop" % "hadoop-client" % "2.2.0",
  "org.apache.hadoop" % "hadoop-common" % "2.2.0"
)

//libraryDependencies ++= Seq(
//	"org.apache.hbase" % "hbase" % "0.94.22",
//	"org.apache.hadoop" % "hadoop-core" % "1.0.4"
//)

libraryDependencies ++= Seq(
  "net.sourceforge.jasa" % "jasa" % "1.2.1-SNAPSHOT",
  "com.espertech" % "esper" % "4.11.0",
  "org.rogach" %% "scallop" % "0.9.5",
  "org.clapper" % "grizzled-slf4j_2.10" % "1.0.2",
  "org.slf4j" % "slf4j-log4j12" % "1.7.7"
)

mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
  {
    case PathList("org", "apache", "jasper", xs @ _*)   => MergeStrategy.first
    case PathList("org", "apache", "commons", xs @ _*) => MergeStrategy.first
    case PathList("javax", "xml", xs @ _*)   => MergeStrategy.first
    case PathList("javax", "servlet", xs @ _*)         => MergeStrategy.first
    case PathList(ps @ _*) if ps.last endsWith ".html" => MergeStrategy.first
    case PathList(ps @ _*) if ps.last endsWith "pom.xml" => MergeStrategy.concat
    case PathList(ps @ _*) if ps.last endsWith "pom.properties" => MergeStrategy.concat
    case PathList(ps @ _*) if ps.last endsWith "spring.tooling" => MergeStrategy.first
    case "log4j.xml" => MergeStrategy.first
    case "pom.xml" => MergeStrategy.concat
    case x => old(x)
  }
}

