package com.packt.akka.models

import spray.json._
import scala.util._
import reactivemongo.bson.{BSONDocumentWriter, BSONDocument, BSONDocumentReader, BSONObjectID}

case class UserEntity(id: BSONObjectID = BSONObjectID.generate,
                      name: String,
                      password: String,
                      following:Option[List[String]]=Option(List("")),
                      followers:Option[List[String]]=Option(List(""))
                      )

object UserEntity {
  implicit def toUserEntity(user: User) = 
    UserEntity(
      name = user.name,
      password = user.password
    )


  implicit object UserEntityBSONReader extends BSONDocumentReader[UserEntity] {
    
    def read(doc: BSONDocument): UserEntity = 
      UserEntity(
        id = doc.getAs[BSONObjectID]("_id").get,
        name = doc.getAs[String]("name").getOrElse("Null"),
        password = doc.getAs[String]("password").getOrElse("Null"),
        following = doc.getAs[List[String]]("following"),
        followers = doc.getAs[List[String]]("followers")

      )
  }

  implicit object UserEntityBSONWriter extends BSONDocumentWriter[UserEntity] {
    def write(userEntity: UserEntity): BSONDocument =
      BSONDocument(
        "_id" -> userEntity.id,
        "name" -> userEntity.name,
        "password" -> userEntity.password,
        "followers"-> userEntity.followers,
        "following"-> userEntity.following
      )
  }

}

object UserEntityProtocol extends DefaultJsonProtocol {

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

  implicit val EntityFormat = jsonFormat5(UserEntity.apply)
}