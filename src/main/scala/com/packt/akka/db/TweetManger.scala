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
//  return Future[List[TweetEntity]]
  def findList(author_id:String)(implicit ec: ExecutionContext) = {
    collection.find(BSONDocument("author_id"->author_id)).cursor[TweetEntity].collect[List]()
  }

  def userTimeLine(id:String)(implicit ec: ExecutionContext):Future[List[TweetEntity]]={
    val content = for{
      user<-UserManager.findByName(id)
      tweets <-getTweetsOfUserList(user.get.following)

    } yield tweets
    println(content.value)
    content
  }
  def getTweetsOfUserList(list: List[BSONObjectID])(implicit ec: ExecutionContext)={
    val res = collection.find(BSONDocument("author_id"->list)).cursor[TweetEntity].collect[List]()
    res
  }
}
