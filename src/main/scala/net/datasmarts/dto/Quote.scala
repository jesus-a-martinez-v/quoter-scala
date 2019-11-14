package net.datasmarts.dto

case class Quote(id: Option[Int] = None, author: String, genre: String, quote: String) {
  require(id.forall(_ >= 0), f"${this.id} is not valid. It must be non-negative.")
  require(author.nonEmpty, "author cannot be empty.")
  require(genre.nonEmpty, "genre cannot be empty.")
  require(quote.nonEmpty, "quote cannot be empty.")
}
