package com.siddartha.languagecl.TweetCollection

import com.google.gson.Gson
import com.siddartha.languagecl.Parsers.TweetCollectOptions
import org.apache.spark.SparkContext
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}


/**
  * Created by siddartha on 11/27/17.
  */
object TweetCollect extends App{
  val options = TweetCollectOptions.parse(args)

  import com.siddartha.twitterClassifier.SparkSetup._

  val streamingContext = new StreamingContext(sc, Seconds(options.intervalInSecs))

  TweetCollector.collect(options, streamingContext, sc)
}

object TweetCollector {
  def collect(options: TweetCollectOptions, streamingContext: StreamingContext, sc: SparkContext): Unit ={
    val tweetStream = TwitterUtils.createStream(streamingContext, None).map(new Gson().toJson(_))

    var tweetsCollected = 0L
    tweetStream.foreachRDD(rdd =>{
      val rddCount = rdd.count()

      if(rddCount > 0){
        rdd.saveAsTextFile(options.tweetDirectory.getAbsolutePath)
        tweetsCollected += rddCount

        if(tweetsCollected > options.numTweetsToCollect) System.exit(0)
      }
    })

    streamingContext.start()
    streamingContext.awaitTermination()
  }
}
