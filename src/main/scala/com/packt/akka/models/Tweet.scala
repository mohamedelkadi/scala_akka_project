package com.packt.akka.models

import spray.json.DefaultJsonProtocol

case class Tweet(id: Int,author_id: String, content: String)

object TweetProtocol extends DefaultJsonProtocol {
  implicit val TweetFormat = jsonFormat3(Tweet.apply)
}
