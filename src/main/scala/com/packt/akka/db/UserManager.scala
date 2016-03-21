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


  private def queryById(id: String) = BSONDocument("_id" ->  BSONObjectID(id))

  private def queryLogin(name: String, password: String) = BSONDocument("name" -> name, "password" -> password)

}