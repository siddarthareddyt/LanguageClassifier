package com.siddartha.languagecl.Train

import com.google.gson.{JsonParser, GsonBuilder}
import com.siddartha.languagecl.Parsers.TrainOptions
import org.apache.spark.mllib.clustering.KMeans

/**
  * Created by siddartha on 11/27/17.
  */
object TrainTweets extends App {
  val options = TrainOptions.parse(args)

  new Trainer(options).train()
}

class Trainer(options: TrainOptions) {

  import options._
  import com.siddartha.twitterClassifier.SparkSetup._

  def train(): Unit = {
    import spark.implicits._

    if (verbose) {
      val tweets = sc.textFile(tweetDirectory.getCanonicalPath)
      println("------------Sample Tweets-------")
      val gson = new GsonBuilder().setPrettyPrinting().create
      val jsonParser = new JsonParser
      tweets.take(5) foreach { tweet =>
        println(gson.toJson(jsonParser.parse(tweet)))
      }
    }

    val tweetsTable = sqlContext.read.json(tweetDirectory.getCanonicalPath)
      .cache()

    tweetsTable.createOrReplaceTempView("tweetsTable")

    if (verbose) {

      println("------Tweets Schema---")
      tweetsTable.printSchema()


      println("----Sample Tweet Text-----")
      sqlContext
        .sql("SELECT text FROM tweetsTable LIMIT 10")
        .collect
        .foreach(println)

      println("------Sample Lang, Name, text---")
      sqlContext
        .sql("SELECT user.lang, user.name, text FROM tweetsTable LIMIT 1000")
        .collect
        .foreach(println)

      println("------Total count by languages Lang, count(*)---")
      sqlContext
        .sql("SELECT user.lang, COUNT(*) as cnt FROM tweetsTable GROUP BY user.lang ORDER BY cnt DESC LIMIT 25")
        .collect
        .foreach(println)

      println("--- Training the model and persisting it")

    }

    val texts = sqlContext.sql("select text from tweetsTable").map(_.toString())
    import com.siddartha.twitterClassifier.featurize

    val vectors = texts.rdd.map(featurize).cache()
    vectors.count()

    val model = KMeans.train(vectors, numClusters, numIterations)

    //sc.makeRDD(model.clusterCenters, numClusters).saveAsObjectFile(modelDirectory.getCanonicalPath)

    model.save(sc, modelDirectory.getCanonicalPath)

    if (verbose) {
      println("----100 tweets from each cluster")
      0 until numClusters foreach { i =>
        println(s"\nCLUSTER $i:")
        texts.take(100) foreach { t =>
          if (model.predict(featurize(t)) == i) println(t)
        }
      }
    }
  }
}
