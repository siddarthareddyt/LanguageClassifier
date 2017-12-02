package com.siddartha

/**
  * Created by siddartha on 11/27/17.
  */
package twitterClassifier {
  import org.apache.spark.SparkContext
  import org.apache.spark.sql.SparkSession

  object SparkSetup {
    val spark = SparkSession
      .builder
      .appName(getClass.getSimpleName.replace("$", ""))
      .getOrCreate()

    val sqlContext = spark.sqlContext

    val sc: SparkContext = spark.sparkContext
    sc.setLogLevel("ERROR")
  }
}
package object twitterClassifier {
  import org.apache.spark.mllib.linalg.Vector
  import org.apache.spark.mllib.feature.HashingTF
  import twitter4j.auth.OAuthAuthorization
  import twitter4j.conf.ConfigurationBuilder

  val numFeatures = 1000
  val tf = new HashingTF(numFeatures)

  def maybeTwitterAuth: Some[OAuthAuthorization] = Some(new OAuthAuthorization(new ConfigurationBuilder().build))

  def featurize(s: String): Vector = tf.transform(s.sliding(2).toSeq)
}