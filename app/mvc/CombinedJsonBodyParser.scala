package mvc

import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Results.BadRequest
import play.api.mvc.{BodyParser, PlayBodyParsers}

import scala.concurrent.ExecutionContext
import scala.util.Try

/**
  *
  * GitHub by default will embed the JSON in the form field 'payload'.
  *
  * To minimise complication in setting up the hook, we will accept both application/json and urlencoded payloads.
  *
  */
object CombinedJsonBodyParser {
  private def urlEncodedParser(
      playBodyParsers: PlayBodyParsers
  )(implicit executionContext: ExecutionContext) =
    playBodyParsers.formUrlEncoded.validate { map =>
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

  def apply(playBodyParsers: PlayBodyParsers)(implicit executionContext: ExecutionContext): BodyParser[JsValue] =
    playBodyParsers.using { requestHeader =>
      if (requestHeader.headers
            .get("Content-Type")
            .contains("application/x-www-form-urlencoded"))
        urlEncodedParser(playBodyParsers)
      else playBodyParsers.tolerantJson
    }

}
