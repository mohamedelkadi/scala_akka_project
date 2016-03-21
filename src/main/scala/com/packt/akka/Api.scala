package com.packt.akka

import spray.json._
import DefaultJsonProtocol._
import akka.actor.ActorSystem
import scala.concurrent.Future
import akka.stream.{ ActorMaterializer, Materializer }
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import com.packt.akka.models._
import akka.http.scaladsl.model.StatusCodes._
import com.packt.akka.db.UserManager
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import scala.concurrent.ExecutionContext

trait RestApi {
  import UserProtocol._
  import UserEntity._
  import UserEntityProtocol.EntityFormat

  implicit val system: ActorSystem

  implicit val materializer: Materializer

  implicit val ec: ExecutionContext

  val route =
    pathPrefix("users"){

        pathPrefix("login"){
          (post & entity(as[User])) { user =>
            complete {
              UserManager.login(user.name, user.password) map { r =>
              //Login -> Map("name" -> r.name, "password" -> r.password).toJson
              OK -> r
              }
            }
          }
        }~
          (get & path(Segment)) { id =>
          complete {
            UserManager.findById(id) map { t =>
              OK -> t
            }
          }
        }~
          (pathPrefix("signup")){
          (post & entity(as[User])) { user =>
            complete {
                UserManager.save(user) map { r =>
                Created -> Map("id" -> r.id).toJson
                //OK -> r
              }
            }
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