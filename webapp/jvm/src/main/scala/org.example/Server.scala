package org.example

import upickle.default.{Reader, Writer, write => uwrite, read => uread}

import akka.http.scaladsl._
import model._

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object Server {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("example")
    import system.dispatcher
    implicit val materializer = ActorMaterializer()
    
    val index = {
      import scalatags.Text.all._
      import scalatags.Text.tags2.title

      "<!DOCTYPE html>" +
      html(
        head(
          title("Example"),
          base(href:="/"),
          meta(charset:="utf-8")
        ),
        body(
          script(src:="/assets/webapp-jsdeps.js"),
          script(src:="/assets/webapp-fastopt.js"),
          script("org.example.Client().main()")
        )
      )
    }
    val home = HttpResponse(entity = HttpEntity(ContentTypes.`text/html(UTF-8)`, index))

    val api = new Api {
      val items = List(
        Stuff("a", "b", Map("foo" -> "bar")),
        Stuff("x", "y", Map("alice" -> "bob")),
        Stuff("x", "y", Map("alice" -> "bob")),
        Stuff("y", "y", Map("alice" -> "bob")),
        Stuff("g", "y", Map("alice" -> "bob")),
        Stuff("t", "y", Map("alice" -> "bob")),
        Stuff("x", "w", Map("alice" -> "bob")),
        Stuff("x", "f", Map("alice" -> "bob")),
        Stuff("x", "y", Map("alice" -> "bob"))
      )
      def find(q: String): Future[List[Stuff]] = {
        Future.successful(items.filter{
          case Stuff(a, b, _) => q == a || q == b
        })
      }
      def stuffPage(a: String, b: String): Future[Option[Stuff]] = {
        Future.successful(items.find{
          case Stuff(sa, sb, _) => a == sa && b == sb
        })
      }
    }

    val route = {
      import akka.http.scaladsl._
      import server.Directives._

      post {
        path("api" / Segments){ s ⇒
          entity(as[String]) { e ⇒
            complete {
              AutowireServer.route[Api](api)(
                autowire.Core.Request(s, uread[Map[String, String]](e))
              )
            }
          }
        }
      } ~
      get {
        path("assets" / Rest) { path ⇒
          getFromResource(path)
        } ~
        pathSingleSlash {
          complete(home)
        } ~
        path("stuff" / Rest) { _ ⇒
          complete(home)
        }
      }
    }
    Await.result(Http().bindAndHandle(route, "localhost", 8080), 20.seconds)

    ()
  } 
}

object AutowireServer extends autowire.Server[String, Reader, Writer]{
  def read[Result: Reader](p: String)  = uread[Result](p)
  def write[Result: Writer](r: Result) = uwrite(r)
}