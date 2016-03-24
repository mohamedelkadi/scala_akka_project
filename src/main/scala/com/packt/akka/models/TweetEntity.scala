package com.packt.akka.models

import spray.json._
import scala.util._
import reactivemongo.bson.{BSONDocumentWriter, BSONDocument, BSONDocumentReader, BSONObjectID}

case class TweetEntity(id: BSONObjectID = BSONObjectID.generate,
                       user_id: String,
                      content: String)

object TweetEntity{
  implicit def toTweetEntity(tweet: Tweet) =
    TweetEntity( user_id=tweet.user_id, content = tweet.content)


  implicit object TweetEntityBSONReader extends BSONDocumentReader[TweetEntity] {
    
    def read(doc: BSONDocument): TweetEntity =
      TweetEntity(
        id = doc.getAs[BSONObjectID]("_id").get,
        user_id = doc.getAs[String]("user_id").get,
        content = doc.getAs[String]("content").get
      )
  }

  implicit object TweetEntityBSONWriter extends BSONDocumentWriter[TweetEntity] {
    def write(tweetEntity: TweetEntity): BSONDocument =
      BSONDocument(
        "_id" -> tweetEntity.id,
        "user_id" -> tweetEntity.user_id,
        "content" -> tweetEntity.content
      )
  }

}

object tweetEntityProtocol extends DefaultJsonProtocol {

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

  implicit val EntityFormat = jsonFormat3(UserEntity.apply)
}