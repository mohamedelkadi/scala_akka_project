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
  
  def findByName(name: String)(implicit ec: ExecutionContext) =
    collection.find(queryById(name)).one[UserEntity]


  def login(name: String, password: String)(implicit ec: ExecutionContext) =
  	collection.find(queryLogin(name, password)).one[UserEntity]


  def follow(userAName: String, userBName: String)(implicit ec: ExecutionContext) = {
    collection.update(querySelector(userBName), queryModifierInsertForFollower(userAName), upsert = true)
    collection.update(querySelector(userAName), queryModifierInsertForFollowing(userBName), upsert = true) }


  def unFollow(userAName: String, userBName: String)(implicit ec: ExecutionContext) = {
    collection.update(querySelector(userBName), queryModifierDeleteForFollower(userAName))
    collection.update(querySelector(userAName), queryModifierDeleteForFollowing(userBName)) }

  // def getUserId(name: String) (implicit ec: ExecutionContext)={
  //   collection.find(BSONDocument("name" -> name), BSONDocument("_id" -> 1)).one[UserEntity]
  // }


  private def queryById(name: String) = BSONDocument("name" ->  name)

  private def queryLogin(name: String, password: String) = BSONDocument("name" -> name, "password" -> password)

  private def querySelector(name: String) = BSONDocument("name" -> name)

  private def queryModifierInsertForFollower(name: String) = BSONDocument("$push" -> BSONDocument("followers" -> name))

  private def queryModifierInsertForFollowing(name: String) = BSONDocument("$push" -> BSONDocument("following" -> name))

  private def queryModifierDeleteForFollower(name: String) = BSONDocument("$pop" -> BSONDocument("followers" -> name))

  private def queryModifierDeleteForFollowing(name: String) = BSONDocument("$pop" -> BSONDocument("following" -> name))

}