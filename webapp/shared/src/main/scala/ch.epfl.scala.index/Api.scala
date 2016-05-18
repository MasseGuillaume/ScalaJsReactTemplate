package org.example

import scala.concurrent.Future

case class Stuff(val a: String, val b: String, z: Map[String, String])

trait Api {
  def find(q: String): Future[List[Stuff]]
  def stuffPage(a: String, b: String): Future[Option[Stuff]]
}