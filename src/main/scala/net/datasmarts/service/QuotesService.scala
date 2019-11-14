package net.datasmarts.service

import net.datasmarts.dto.Quote
import net.datasmarts.model.Quotes
import slick.jdbc.PostgresProfile.api._
import slick.lifted.TableQuery

import scala.concurrent.{ExecutionContext, Future}

class QuotesService(implicit db: Database, ec: ExecutionContext) {
  private val quotes = TableQuery[Quotes]

  def populate(dataPath: String) {
    val bufferedSource = io.Source.fromFile(dataPath)

    val allQuotes = for {
      (line, index) <- bufferedSource.getLines().zipWithIndex
    } yield {
      val Array(text, author, genre) = line.trim.split(";")
      Quote(id = Some(index), quote = text, author = author, genre = genre)
    }

    insertQuotes(allQuotes.toSeq)
  }

  def createSchema(): Future[Unit] = {
    db.run(quotes.schema.createIfNotExists)
  }

  def getQuotes(author: Option[String] = None, genre: Option[String]): Future[Seq[Quote]] = {
    db.run {
      quotes
        .filterOpt(author)(_.author === _)
        .filterOpt(genre)(_.genre === _)
        .result
    }
  }

  def getQuoteById(id: Int): Future[Option[Quote]] = {
    db.run {
      quotes.filter(_.id === id).result.headOption
    }
  }

  def deleteQuoteById(id: Int): Future[Boolean] = {
    db.run {
      quotes.filter(_.id === id).delete.map(_ > 0)
    }
  }

  def insertQuote(quote: Quote): Future[Quote] = {
    db.run {
      (quotes returning quotes).forceInsert(quote)
    }
  }

  def insertQuotes(quoteSeq: Seq[Quote]): Unit = {
    db.run {
      quotes.forceInsertAll(quoteSeq)
    }
  }
}
