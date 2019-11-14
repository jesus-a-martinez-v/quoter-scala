package net.datasmarts

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import net.datasmarts.controller.QuotesController
import net.datasmarts.service.QuotesService
import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._

import scala.util.{Failure, Success}

object Server extends App {
  private val DATA_PATH = "/home/jesus/Desktop/Portfolio/quoter-scala/src/main/resources/quotes_all.csv"

  private implicit val db: PostgresProfile.backend.Database = Database.forConfig("psql")
  private implicit val system = ActorSystem("quoter")
  private implicit val materializer = ActorMaterializer()
  private implicit val executionContext = system.dispatcher

  private val quotesService = new QuotesService()
  private val quotesController = new QuotesController(quotesService)

  quotesService.createSchema().andThen {
    case Success(_) => quotesService.populate(DATA_PATH)
    case Failure(exception) => println(s"Error creating db: $exception")
  }

  Http().bindAndHandle(quotesController.routes, "localhost", 8080)
}
