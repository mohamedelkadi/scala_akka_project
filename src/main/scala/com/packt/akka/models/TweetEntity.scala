package com.packt.akka.models

import spray.json._
import scala.util._
import reactivemongo.bson.{BSONDocumentWriter, BSONDocument, BSONDocumentReader, BSONObjectID}

case class TweetEntity(_id: BSONObjectID = BSONObjectID.generate,
                       id:Int,
                       author_id: Int,
                      content: String)

object TweetEntity{
  implicit def toTweetEntity(tweet: Tweet) =
    TweetEntity(id=tweet.id, author_id=tweet.author_id, content = tweet.content)


  implicit object TweetEntityBSONReader extends BSONDocumentReader[TweetEntity] {
    
    def read(doc: BSONDocument): TweetEntity ={
      TweetEntity(
        _id = doc.getAs[BSONObjectID]("_id").getOrElse(BSONObjectID.generate),
        id = doc.getAs[Int]("id").getOrElse(0),
        author_id = doc.getAs[Int]("author_id").getOrElse(0),
        content = doc.getAs[String]("content").getOrElse("not found")
      )
    }
  }

  implicit object TweetEntityBSONWriter extends BSONDocumentWriter[TweetEntity] {
    def write(tweetEntity: TweetEntity): BSONDocument =
      BSONDocument(
        "_id" -> tweetEntity._id,
        "author_id" -> tweetEntity.author_id,
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