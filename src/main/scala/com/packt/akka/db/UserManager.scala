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


  def signUp(userEntity: UserEntity)(implicit ec: ExecutionContext) =
    collection.insert(userEntity).map(_ => Created(userEntity.id.stringify))
  
  def findById(id: String)(implicit ec: ExecutionContext) =
    collection.find(queryById(id)).one[UserEntity]


  def login(name: String, password: String)(implicit ec: ExecutionContext) =
  	collection.find(queryLogin(name, password)).one[UserEntity]


  def follow(userAId: String, userBId: String)(implicit ec: ExecutionContext) = {
    collection.update(querySelector(userBId), queryModifierInsertForFollower(userAId), upsert = true)
    collection.update(querySelector(userAId), queryModifierInsertForFollowing(userBId), upsert = true) }


  def unFollow(userAId: String, userBId: String)(implicit ec: ExecutionContext) = {
    collection.update(querySelector(userBId), queryModifierDeleteForFollower(userAId))
    collection.update(querySelector(userAId), queryModifierDeleteForFollowing(userBId)) }


  private def queryById(id: String) = BSONDocument("_id" ->  BSONObjectID(id))

  private def queryLogin(name: String, password: String) = BSONDocument("name" -> name, "password" -> password)

  private def querySelector(id: String) = BSONDocument("_id" -> BSONObjectID(id))

  private def queryModifierInsertForFollower(id: String) = BSONDocument("$push" -> BSONDocument("followers" -> BSONObjectID(id)))

  private def queryModifierInsertForFollowing(id: String) = BSONDocument("$push" -> BSONDocument("following" -> BSONObjectID(id)))

  private def queryModifierDeleteForFollower(id: String) = BSONDocument("$pop" -> BSONDocument("followers" -> BSONObjectID(id)))

  private def queryModifierDeleteForFollowing(id: String) = BSONDocument("$pop" -> BSONDocument("following" -> BSONObjectID(id)))

}