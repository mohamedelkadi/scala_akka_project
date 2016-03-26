package com.packt.akka.db

/**
  * Created by molhm on 24/03/16.
  */

import akka.http.scaladsl.server.Route
import com.packt.akka.models._
import reactivemongo.api.QueryOpts
import reactivemongo.core.commands.Count
import scala.concurrent.{Future, ExecutionContext}
import reactivemongo.bson.{BSONDocument, BSONObjectID}
import reactivemongo.api.collections.bson.BSONCollection

object TweetManger {
  def delete(id: Int)(implicit ec: ExecutionContext) ={
    collection.remove(BSONDocument("id"->id))
  }


  import  MongoDB._
  import TweetEntity._

  def add(tweet: TweetEntity)(implicit ec: ExecutionContext)={
    collection.insert(tweet)
  }
  val collection = db[BSONCollection]("tweets")
  //return Future[Option[TweetEntity]]
  def findById(id:Int)(implicit ec: ExecutionContext)={
        collection.find(BSONDocument("id"->id)).one[TweetEntity]
  }
}
