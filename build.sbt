import _root_.sbtassembly.AssemblyPlugin.autoImport._
import _root_.sbtassembly.PathList

name := "LanguageClassifier"

version := "1.0"

scalaVersion := "2.11.12"


scalaVersion := "2.11.12"

val sparkVersion = "2.2.0"
resolvers += "Databricks Repository" at "https://dl.bintray.com/spark-packages/maven/"

libraryDependencies ++= Seq(
  "org.apache.spark"      %%        "spark-core"              %   sparkVersion, // % Provided excludeAll ExclusionRule("org.scalatest"),
  "org.apache.spark"      %%        "spark-sql"               %   sparkVersion, // % Provided excludeAll ExclusionRule("org.scalatest"),
  "org.apache.spark"      %%        "spark-mllib"             %   sparkVersion,
  "org.apache.spark"      %%        "spark-hive"              %   sparkVersion, // % Provided excludeAll ExclusionRule("org.scalatest"),
  "org.apache.spark"      %%        "spark-yarn"              %   sparkVersion, // % Provided excludeAll ExclusionRule("org.scalatest"),
  "org.apache.spark"      %%        "spark-streaming"         %   sparkVersion,
  "org.apache.spark"      %%        "spark-streaming-kafka-0-8" % sparkVersion,
  "org.apache.bahir"      %%        "spark-streaming-twitter"   % sparkVersion,
  "org.twitter4j"         %         "twitter4j-core"            % "4.0.6",
  "com.google.code.gson"  %         "gson"                      % "2.8.2" withSources(),
  "com.jsuereth"          %%        "scala-arm"                 % "2.0",
  "com.github.acrisci"    %%        "commander"                 % "0.1.0" excludeAll (
      ExclusionRule(organization = "org.scalatest")
    ),
  "databricks" % "spark-corenlp" % "0.2.0-s_2.10"

)

assemblyMergeStrategy in assembly := {
  case PathList("org", "apache", "spark", "streaming", "twitter", _*) => MergeStrategy.deduplicate
  case PathList("org", "apache", "spark", _*) => MergeStrategy.discard
  case PathList("org", "spark_project", _*) => MergeStrategy.discard
  case m if m.toLowerCase.endsWith("manifest.mf") => MergeStrategy.discard
  case m if m.toLowerCase.matches("meta-inf.*\\.sf$") => MergeStrategy.discard
  case "log4j.properties" => MergeStrategy.deduplicate
  case m if m.toLowerCase.startsWith("meta-inf/services/") => MergeStrategy.filterDistinctLines
  case "reference.conf" => MergeStrategy.concat
  case _ => MergeStrategy.first
}