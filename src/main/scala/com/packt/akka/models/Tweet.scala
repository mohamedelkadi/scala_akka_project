package com.packt.akka.models

import spray.json.DefaultJsonProtocol

case class Tweet(user_id: String, content: String)

object TweetProtocol extends DefaultJsonProtocol {
  implicit val TweetFormat = jsonFormat2(Tweet.apply)
}
