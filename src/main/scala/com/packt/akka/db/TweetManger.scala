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
  def delete(id: Int)(implicit ec: ExecutionContext) = {
    collection.remove(BSONDocument("id" -> id))
  }


  import MongoDB._
  import TweetEntity._

  def add(tweet: TweetEntity)(implicit ec: ExecutionContext) = {
    collection.insert(tweet)
  }

  val collection = db[BSONCollection]("tweets")

  //return Future[Option[TweetEntity]]
  def findById(id: Int)(implicit ec: ExecutionContext) = {
    collection.find(BSONDocument("id" -> id)).one[TweetEntity]
  }

  //  return Future[List[TweetEntity]]
  def findList(author_name: String)(implicit ec: ExecutionContext) = {
    collection.find(BSONDocument("author_name" -> author_name)).cursor[TweetEntity].collect[List]()
  }

  def userTimeLine(username: String)(implicit ec: ExecutionContext)
  : Future[List[TweetEntity]] = {
    for {
      user <- UserManager.findByName(username)
      tweets <- getTweetsOfUserList(user.get.following.get)

    } yield tweets

  }

  def getTweetsOfUserList(list: List[String])(implicit ec: ExecutionContext) =
  {
    val res = collection.find(BSONDocument("author_name" -> BSONDocument("$in" -> list.toArray))).cursor[TweetEntity].collect[List]()
    res
  }
}
