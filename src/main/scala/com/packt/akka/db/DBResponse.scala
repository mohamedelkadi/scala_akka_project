package com.packt.akka.db

case class GetUserById(id: String)

case class Created(id: String)

case class Login(username: String, password: String)