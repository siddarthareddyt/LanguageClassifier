package com.siddartha.languagecl.Prediction

import com.siddartha.languagecl.Parsers.PredictOptions
import org.apache.spark.SparkContext
import org.apache.spark.mllib.clustering.KMeansModel
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Created by siddartha on 11/27/17.
  */
object Predict extends App{
  val options = PredictOptions.parse(args)

  import com.siddartha.twitterClassifier.SparkSetup._

  val streamingContext = new StreamingContext(sc, Seconds(options.intervalInSecs))

  Predictor.predict(streamingContext, sc, options)
}

object Predictor{
  def predict(streamingContext: StreamingContext, sc: SparkContext, options: PredictOptions): Unit = {
    println("------Loading model---------")

    val model: KMeansModel = KMeansModel.load(sc, options.modelDirectory.getCanonicalPath)

    import com.siddartha.twitterClassifier.featurize

    TwitterUtils.createStream(streamingContext, None)
      .map(_.getText)
      .foreachRDD(rdd =>{
        println("----------Predicting the tweets----------")
        rdd.take(10).foreach(r => println(s"----------predicted cluster is ----- ${model.predict(featurize(r))}"))
      })

    streamingContext.start()
    streamingContext.awaitTermination()
  }
}