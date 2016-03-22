package com.packt.akka.db 

import com.packt.akka.models._
import reactivemongo.api.QueryOpts
import reactivemongo.core.commands.Count
import scala.concurrent.ExecutionContext
import reactivemongo.bson.{BSONDocument, BSONObjectID}
import reactivemongo.api.collections.bson.BSONCollection

object UserManager {
  import MongoDB._
  import UserEntity._

  val collection = db[BSONCollection]("users")


  def save(userEntity: UserEntity)(implicit ec: ExecutionContext) =
    collection.insert(userEntity).map(_ => Created(userEntity.id.stringify))
  
  def findById(id: String)(implicit ec: ExecutionContext) =
    collection.find(queryById(id)).one[UserEntity]


  def login(name: String, password: String)(implicit ec: ExecutionContext) =
  	collection.find(queryLogin(name, password)).one[UserEntity]


  def follow(userAId: String, userBId: String)(implicit ec: ExecutionContext) = {
    collection.update(querySelector(userBId), queryModifierForFollower(userAId), upsert = true)
    collection.update(querySelector(userAId), queryModifierForFollowing(userBId), upsert = true) }


  private def queryById(id: String) = BSONDocument("_id" ->  BSONObjectID(id))

  private def queryLogin(name: String, password: String) = BSONDocument("name" -> name, "password" -> password)

  private def querySelector(id: String) = BSONDocument("_id" -> BSONObjectID(id))

  private def queryModifierForFollower(id: String) = BSONDocument("$push" -> BSONDocument("followers" -> BSONObjectID(id)))

  private def queryModifierForFollowing(id: String) = BSONDocument("$push" -> BSONDocument("following" -> BSONObjectID(id)))

}