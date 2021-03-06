package com.packt.akka

import reactivemongo.bson.BSONObjectID
import spray.json._
import DefaultJsonProtocol._
import akka.actor.ActorSystem
import scala.concurrent.Future
import akka.stream.{ActorMaterializer, Materializer}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import com.packt.akka.models._
import akka.http.scaladsl.model.StatusCodes._
import com.packt.akka.db.UserManager
import com.packt.akka.db.TweetManger
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import scala.concurrent.ExecutionContext
import TweetProtocol.TweetFormat

trait RestApi {

  import UserProtocol._
  import UserEntity._
  import TweetEntityProtocol.EntityFormat2
  import UserEntityProtocol.EntityFormat

  implicit val system: ActorSystem

  implicit val materializer: Materializer

  implicit val ec: ExecutionContext

  val route =
    pathPrefix("users") {

          pathPrefix("login") {
            (post & entity(as[User])) { user =>
              complete{

                def result(xx: Option[UserEntity]) = xx match {
                  case Some(r) => r
                  case None    => "Not Found"
                }
                UserManager.login(user.name, user.password) map { 
                  r => println(result(r))
                  OK -> "done"  
                }
              }
            }
      } ~
        (get & path(Segment)) { name =>
          complete {
            UserManager.findByName(name) map { t =>
              OK -> t
            }
          }
        } ~
        (get & path(Segment / "tweets")) { name =>
          complete {
            TweetManger.findList(name).map {
              r => OK -> r.toJson
            }
          }
        } ~
        (get & path(Segment / "home")) { name =>
          complete {
            TweetManger.userTimeLine(name).map {
              r => OK -> r.toJson
            }
          }
        }~
        (pathPrefix("signup")) {
          (post & entity(as[User])) { user =>
            complete {
              UserManager.signUp(user) map { r =>
                Created -> Map("id" -> r.id).toJson
              }
            }
          }
        } ~
        (get & path(Segment / "follow" / Segment)) { (userAName, userBName) =>
          complete {
            UserManager.follow(userAName, userBName)
            Map("status" -> " OK ").toJson
          }
        } ~
        (get & path(Segment / "unfollow" / Segment)) { (userAName, userBName) =>
          complete {
            UserManager.unFollow(userAName, userBName)
            Map("status" -> " OK ").toJson
          }
        }
    } ~
      pathPrefix("tweets") {
//        (get) {
//          complete {
//            TweetManger.findList("").map {
//              r => OK -> r.toJson
//            }
//          }
//        } ~
          (get & path(Segment)) {
            id => complete {
              TweetManger.findById(id.toInt) map {
                r => OK -> r
              }
            }

          } ~
          (delete & path(Segment)) {
            id => complete {
              TweetManger.delete(id.toInt)
              Map("status" -> " Deleted ").toJson

            }

          } ~
          (post & entity(as[Tweet])) {
            tweet => complete {
              println(tweet)
              TweetManger.add(tweet)
              Map("status" -> " Added ").toJson
            }

          }

      }
}

object Api extends App with RestApi {

  override implicit val system = ActorSystem("rest-api")

  override implicit val materializer = ActorMaterializer()

  override implicit val ec = system.dispatcher

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  Console.readLine()

  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.shutdown())

}