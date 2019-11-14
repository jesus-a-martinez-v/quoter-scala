package net.datasmarts.dto

import spray.json.DefaultJsonProtocol

object JsonFormats {
  import DefaultJsonProtocol._

  implicit val quoteJsonFormat = jsonFormat4(Quote)
}
