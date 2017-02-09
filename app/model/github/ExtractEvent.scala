package model.github

import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{BodyParser, BodyParsers, Request}

import scala.concurrent.ExecutionContext
import scala.util.Try

/**
  * Created by me on 08/02/2017.
  */
case class ExtractEvent(repositoryUrl: String) {

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

  case class AtRequest(request: Request[JsValue]) {
    def githubEvent: Option[ExtractEvent] = {
      for {
        eventType <- request.headers.get("X-GitHub-Event")
        if "push" == eventType
        repositoryUri <- (request.body \ "repository" \ "html_url").asOpt[String]
      } yield ExtractEvent(repositoryUri)
    }

    def bitbucketEvent: Option[ExtractEvent] = {
      for {
        eventType <- request.headers.get("X-Event-Key")
        if "repo:push" == eventType
        repositoryUri <- (request.body \ "repository" \ "links" \ "html" \ "href").asOpt[String]
      } yield ExtractEvent(repositoryUri)
    }

    def gitlabEvent: Option[ExtractEvent] = {
      for {
        eventType <- request.headers.get("X-Gitlab-Event")
        if "Push Hook" == eventType
        repositoryUrl <- (request.body \ "repository" \ "homepage").asOpt[String]
      } yield ExtractEvent(repositoryUrl)
    }

    def anyEvent: Option[ExtractEvent] = {
      githubEvent orElse bitbucketEvent orElse gitlabEvent
    }
  }

}
