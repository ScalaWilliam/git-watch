package model

import play.api.libs.EventSource.Event
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Results._
import play.api.mvc.{BodyParser, BodyParsers, Request}

import scala.concurrent.ExecutionContext
import scala.util.Try

/**
  * Created by me on 08/02/2017.
  */
case class PushEvent(repositoryUrl: String) {
  def toEventSource: Event =
    Event(
      id = None,
      name = Some("push"),
      data = repositoryUrl
    )
}

object PushEvent {

  private def urlEncodedParser(implicit executionContext: ExecutionContext) = {
    BodyParsers.parse.urlFormEncoded.validate { map =>
      map
        .get("payload")
        .flatMap(_.headOption)
        .map { jsonString =>
          Try(Json.parse(jsonString))
            .map(Right.apply)
            .getOrElse(Left("Could not parse JSON"))
        }
        .getOrElse(Left("Not found field 'payload"))
        .left
        .map(str => BadRequest(str))
    }
  }

  def combinedParser(
      implicit executionContext: ExecutionContext): BodyParser[JsValue] = {
    BodyParsers.parse.using { requestHeader =>
      if (requestHeader.headers
            .get("Content-Type")
            .contains("application/x-www-form-urlencoded"))
        urlEncodedParser
      else BodyParsers.parse.tolerantJson
    }
  }

  case class AtRequest(request: Request[JsValue]) {
    def githubEvent: Option[PushEvent] = {
      for {
        eventType <- request.headers.get("X-GitHub-Event")
        if "push" == eventType
        repositoryUri <- (request.body \ "repository" \ "html_url")
          .asOpt[String]
      } yield PushEvent(repositoryUri)
    }

    def bitbucketEvent: Option[PushEvent] = {
      for {
        eventType <- request.headers.get("X-Event-Key")
        if "repo:push" == eventType
        repositoryUri <- (request.body \ "repository" \ "links" \ "html" \ "href")
          .asOpt[String]
      } yield PushEvent(repositoryUri)
    }

    def gitlabEvent: Option[PushEvent] = {
      for {
        eventType <- request.headers.get("X-Gitlab-Event")
        if "Push Hook" == eventType
        repositoryUrl <- (request.body \ "repository" \ "homepage")
          .asOpt[String]
      } yield PushEvent(repositoryUrl)
    }

    def anyEvent: Option[PushEvent] = {
      githubEvent orElse bitbucketEvent orElse gitlabEvent
    }
  }

}
