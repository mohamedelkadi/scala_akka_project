package com.packt.akka.models

import spray.json.DefaultJsonProtocol

case class User(name: String, password: String)

object UserProtocol extends DefaultJsonProtocol {
  implicit val UserFormat = jsonFormat2(User.apply)
}
