package com.packt.akka.models

import spray.json._
import scala.util._
import reactivemongo.bson.{BSONDocumentWriter, BSONDocument, BSONDocumentReader, BSONObjectID}

case class TweetEntity(id:Int,
                       author_name:String,
                      content: String,
                       _id: BSONObjectID = BSONObjectID.generate)

object TweetEntity{
  implicit def toTweetEntity(tweet: Tweet) =
    TweetEntity(id=tweet.id, author_name=tweet.author_name, content = tweet.content)


  implicit object TweetEntityBSONReader extends BSONDocumentReader[TweetEntity] {
    
    def read(doc: BSONDocument): TweetEntity ={
      TweetEntity(
        _id = doc.getAs[BSONObjectID]("_id").get,
        id = doc.getAs[Int]("id").getOrElse(0),
        author_name = doc.getAs[String]("author_name").getOrElse("Wrong  user info "),
        content = doc.getAs[String]("content").getOrElse("not found")
      )
    }
  }

  implicit object TweetEntityBSONWriter extends BSONDocumentWriter[TweetEntity] {
    def write(tweetEntity: TweetEntity): BSONDocument =
      BSONDocument(
        "_id" -> tweetEntity._id,
        "id"->tweetEntity.id,
        "author_name" -> tweetEntity.author_name,
        "content" -> tweetEntity.content
      )
  }

}

object TweetEntityProtocol extends DefaultJsonProtocol {

  implicit object BSONObjectIdProtocol extends RootJsonFormat[BSONObjectID] {

    override def write(obj: BSONObjectID): JsValue = JsString(obj.stringify)
    override def read(json: JsValue): BSONObjectID = json match {

      case JsString(id) => BSONObjectID.parse(id) match {
        case Success(validId) => validId
        case _ => deserializationError("Invalid BSON Object Id")
      }
      case _ => deserializationError("BSON Object Id expected")
    }
  }

  implicit val EntityFormat2 = jsonFormat4(TweetEntity.apply)
}