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
  

  import  MongoDB._
  import TweetEntity._
  import UserManager._

  val collection = db[BSONCollection]("tweets")

  def delete(id: Int)(implicit ec: ExecutionContext) ={
    collection.remove(BSONDocument("id"->id))
  }


  def add(tweet: TweetEntity)(implicit ec: ExecutionContext)={
    collection.insert(tweet)
  }
  
  //return Future[Option[TweetEntity]]
  def findById(id:Int)(implicit ec: ExecutionContext)={
        collection.find(BSONDocument("id"->id)).one[TweetEntity]
  }
//  return Future[List[BSONDocument]]
  def findList(author_name:String)(implicit ec: ExecutionContext) = {
        if(author_name != "")
          collection.find(BSONDocument( "author_name" -> author_name )).cursor[TweetEntity].collect[List]()
        else
          collection.find(BSONDocument()).cursor[TweetEntity].collect[List]()
  }
}
