name := "lumen-experiments"

version := "0.1"

scalaVersion := "2.10.5"

//Spark
libraryDependencies := Seq(
  "org.apache.spark" %% "spark-core"  % "2.0.0" % "provided",
  "org.apache.spark" %% "spark-streaming" % "2.0.0" % "provided",
  "org.apache.spark" %% "spark-hive" % "2.0.0" % "provided"
)

//project specific
libraryDependencies ++= Seq (
  "com.beust" % "jcommander" % "1.48",
  "junit" % "junit" % "4.8.1" % "test",
  "joda-time" % "joda-time" % "2.9",
  "com.fasterxml.uuid" % "java-uuid-generator" % "3.1.4",
  "com.fanatics.dataplatform" % "sparkle-2-0-0_2.10" % "0.8",
  "org.postgresql" % "postgresql" % "9.4.1211.jre7",
  "com.amazonaws" % "aws-java-sdk-sns" % "1.10.30",
  "com.amazonaws" % "aws-java-sdk-sns" % "1.10.30",
  "com.amazonaws" % "aws-java-sdk-s3" % "1.10.30",
  "net.liftweb" % "lift-json-ext_2.11" % "2.6.3",
  "com.thoughtworks.paranamer" % "paranamer" % "2.3",
  "com.databricks" % "spark-csv_2.10" % "1.3.2",
  "org.apache.commons" % "commons-csv" % "1.1"
)

//test
libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.6" % "test"

mainClass := Some("com.fanatics.data.platform.lumen.experiments.LumenDataGenerator")

resolvers ++= Seq("Concurrent Maven Repo" at "http://conjars.org/repo",
  "Internal Release libraries" at "http://repo.fanatics.corp/artifactory/libs-release-local/",
  "Internal Snapshot libraries" at "http://repo.fanatics.corp/artifactory/libs-snapshot-local/")
credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

publishTo := {
  if (isSnapshot.value)
    Some("Artifactory" at "http://repo.fanatics.corp/artifactory/libs-snapshot-local/")
  else
    Some("Artifactory" at "http://repo.fanatics.corp/artifactory/libs-release-local/")
}

assemblyMergeStrategy in assembly <<= (mergeStrategy in assembly) { mergeStrategy => {
  case entry => {
    val strategy = mergeStrategy(entry)
    if (strategy == MergeStrategy.deduplicate) MergeStrategy.first
    else strategy
  }
}
}
test in assembly := {}

artifact in (Compile, assembly) := {
  val art = (artifact in (Compile, assembly)).value
  art.copy(`classifier` = Some("assembly"))
}

addArtifact(artifact in (Compile, assembly), assembly)


//For Java 8
javaOptions in Test ++= Seq("-Xms128M", "-Xmx4096M","-XX:+CMSClassUnloadingEnabled", "-XX:+UseConcMarkSweepGC", "-XX:MetaspaceSize=512m", "-XX:MaxMetaspaceSize=1024m")

//For Java 7
//javaOptions in Test ++= Seq("-Xms128M", "-Xmx2048M", "-

