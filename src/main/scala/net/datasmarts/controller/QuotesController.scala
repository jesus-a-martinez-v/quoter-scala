package net.datasmarts.controller

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import net.datasmarts.dto.Quote
import net.datasmarts.service.QuotesService

import scala.concurrent.ExecutionContext

class QuotesController(private val quotesService: QuotesService)(implicit ec: ExecutionContext) {

  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import net.datasmarts.dto.JsonFormats._
  import spray.json.DefaultJsonProtocol._

  val routes = pathPrefix("quotes") {
    concat(
      pathEndOrSingleSlash {
        concat(
          post {
            entity(as[Quote]) { quote =>
              onSuccess(quotesService.insertQuote(quote)) { createdQuote =>
                complete((StatusCodes.Created, createdQuote))
              }
            }
          },
          get {
            parameters('author.?, 'genre.?) { (author, genre) =>
              onSuccess(quotesService.getQuotes(author, genre)) { quotes =>
                complete(StatusCodes.OK, quotes)
              }
            }
          }
        )
      },
      path(IntNumber) { quoteId =>
        concat(
          get {
            onSuccess(quotesService.getQuoteById(quoteId)) {
              case Some(quote) => complete(StatusCodes.OK, quote)
              case None => complete(StatusCodes.NotFound)
            }
          },
          delete {
            onSuccess(quotesService.deleteQuoteById(quoteId)) {
              case true => complete(StatusCodes.OK)
              case false => complete(StatusCodes.NotFound)
            }
          }
        )
      }
    )
  }
}
