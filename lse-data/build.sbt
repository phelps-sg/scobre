import AssemblyKeys._

assemblySettings

name := "lse-data"

version := "0.2"

scalaVersion := "2.10.2"

scalaSource in Compile := file("src/")

resolvers += "Apache HBase" at "https://repository.apache.org/content/repositories/releases"

resolvers += "Thrift" at "http://people.apache.org/~rawson/repo/"

libraryDependencies ++= Seq(
 "org.apache.hadoop" % "hadoop-core" % "0.20.2",
 "org.apache.hbase" % "hbase" % "0.90.4"
)

libraryDependencies <+= (scalaVersion)("org.scala-lang" % "scala-compiler" % _)

resolvers ++= Seq(
  "Sonatype Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
  "Sonatype Releases" at "http://oss.sonatype.org/content/repositories/releases"
)

libraryDependencies ++= Seq(
  "org.scala-saddle" %% "saddle-core" % "1.3.+"
  // (OPTIONAL) "org.scala-saddle" %% "saddle-hdf5" % "1.3.+"
)

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "se.scalablesolutions.akka" % "akka-actor" % "1.2",
  "se.scalablesolutions.akka" % "akka-stm" % "1.2",
  "se.scalablesolutions.akka" % "akka-remote" % "1.2"
)

libraryDependencies += "org.rogach" %% "scallop" % "0.9.4"

libraryDependencies ++= List(
  "com.typesafe.slick" %% "slick" % "1.0.1",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "com.h2database" % "h2" % "1.3.166",
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
    case PathList("javax", "servlet", xs @ _*)         => MergeStrategy.first
    case PathList(ps @ _*) if ps.last endsWith ".html" => MergeStrategy.first
    case "log4j.xml" => MergeStrategy.concat
    case x => old(x)
  }
}
