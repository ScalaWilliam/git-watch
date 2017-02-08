package model.github

import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{BodyParser, BodyParsers, Request}

import scala.concurrent.ExecutionContext
import scala.util.Try

/**
  * Created by me on 08/02/2017.
  */
case class ExtractEvent(eventType: String, repositoryUrls: List[String]) {

}

object ExtractEvent {

  private def urlEncodedParser(implicit executionContext: ExecutionContext) = {
    BodyParsers.parse.urlFormEncoded.validate { map =>
      import play.api.mvc.Results._
      map
        .get("payload")
        .flatMap(_.headOption)
        .map { jsonString =>
          Try(Json.parse(jsonString))
            .map(Right.apply)
            .getOrElse(Left("Could not parse JSON"))
        }
        .getOrElse(Left("Not found field 'payload"))
        .left.map(str => BadRequest(str))
    }
  }

  def combinedParser(implicit executionContext: ExecutionContext): BodyParser[JsValue] = {
    BodyParsers.parse.using { requestHeader =>
      if (requestHeader.headers.get("Content-Type").contains("application/x-www-form-urlencoded"))
        urlEncodedParser
      else BodyParsers.parse.tolerantJson
    }
  }

  def fromJsonRequest(request: Request[JsValue]): Option[ExtractEvent] = {
    val urlKeys = List("html_url", "git_url", "ssh_url", "clone_url")
    for {
      eventType <- request.headers.get("X-GitHub-Event")
      repositoryUrls = urlKeys.flatMap { key =>
        (request.body \ "repository" \ "key").asOpt[String]
      }
      if repositoryUrls.nonEmpty
    } yield ExtractEvent(repositoryUrls = repositoryUrls, eventType = eventType)
  }
}
