import AssemblyKeys._

assemblySettings

name := "lse-data"

version := "0.11"

scalaVersion := "2.10.4"
//scalaVersion := "2.11.0-M5"

scalaSource in Compile := file("src/")

resolvers += "Apache HBase" at "http://repository.apache.org/content/repositories/releases"

resolvers += "JASA" at "http://jasa.sourceforge.net/mvn-repo/jasa"

resolvers += "JABM" at "http://jabm.sourceforge.net/mvn-repo/jabm"

//resolvers += "Thrift" at "http://people.apache.org/~rawson/repo/"

libraryDependencies ++= Seq(
	"org.apache.hbase" % "hbase-client" % "0.98.5-hadoop1",
	"org.apache.hbase" % "hbase-common" % "0.98.5-hadoop1",
	"org.apache.hadoop" % "hadoop-core" % "1.2.1"
)

libraryDependencies += "net.sourceforge.jasa" % "jasa" % "1.2.1-SNAPSHOT"

//libraryDependencies <+= (scalaVersion)("org.scala-lang" % "scala-compiler" % _)

//resolvers ++= Seq(
//  "Sonatype Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
//  "Sonatype Releases" at "http://oss.sonatype.org/content/repositories/releases"
//)

//libraryDependencies ++= Seq(
 // "org.scala-saddle" %% "saddle-core" % "1.3.+"
  // (OPTIONAL) "org.scala-saddle" %% "saddle-hdf5" % "1.3.+"
//)

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

// libraryDependencies += "com.gravity" % "gravity-hpaste" % "0.1.11" withSources()

// resolvers += "Apache HBase" at "https://repository.apache.org/content/repositories/releases"

//resolvers += "Thrift" at "http://people.apache.org/~rawson/repo/"
//
//libraryDependencies ++= Seq(
//  "com.typesafe.akka" % "akka-actor" % "2.0.3",
//  "com.typesafe.akka" % "akka-remote" % "2.0.3"
//)

libraryDependencies += "com.espertech" % "esper" % "4.11.0"

libraryDependencies += "org.rogach" %% "scallop" % "0.9.4"

libraryDependencies += "org.clapper" % "grizzled-slf4j_2.10" % "1.0.1"

libraryDependencies ++= List(
  "com.typesafe.slick" %% "slick" % "1.0.1",
  //"org.slf4j" % "slf4j-nop" % "1.6.4",
//  "com.h2database" % "h2" % "1.3.166",
  "org.xerial" % "sqlite-jdbc" % "3.7.2",
  "mysql" % "mysql-connector-java" % "5.1.23"
/*
  "org.apache.derby" % "derby" % "10.9.1.0",
  "org.hsqldb" % "hsqldb" % "2.2.8",
  "postgresql" % "postgresql" % "9.1-901.jdbc4",
  "net.sourceforge.jtds" % "jtds" % "1.2.4" % "test"
*/
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

net.virtualvoid.sbt.graph.Plugin.graphSettings