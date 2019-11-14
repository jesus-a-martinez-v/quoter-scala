package net.datasmarts.model

import net.datasmarts.dto.Quote
import slick.jdbc.PostgresProfile.api._
import slick.lifted.Tag

class Quotes(tag: Tag) extends Table[Quote](tag, "quotes") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def author = column[String]("author")

  def genre = column[String]("genre")

  def quote = column[String]("quote")

  def * = (id.?, author, genre, quote) <> (Quote.tupled, Quote.unapply)

  def uniqueIndex = index("unique_quote", (author, genre, quote), unique = true)
}