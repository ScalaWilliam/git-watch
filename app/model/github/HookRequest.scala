package model.github

import org.apache.commons.codec.digest.HmacUtils
import play.api.libs.json.JsObject

/**
  * Created by me on 17/08/2016.
  */

case class HookRequest(requestId: String, signature: Option[String], eventType: EventType, body: String, bodyJson: JsObject, repositoryName: String) {
  def signatureValid(secret: String): Option[Boolean] = {
    signature.map {
      case HookRequest.parseSignature(sv) =>
        val resSignature = HmacUtils.hmacSha1Hex(secret, body)
        sv == resSignature
      case _ => false
    }
  }
}

object HookRequest {
  val parseSignature = """^sha1=(.*)$""".r

  def extract(headers: Map[String, List[String]], body: String, bodyJson: JsObject): Option[HookRequest] = {
    for {
      eventType <- headers.get("X-Github-Event").toList.flatMap(_.flatMap(EventType.unapply)).headOption
      signature = headers.get("X-Hub-Signature").toList.flatten.headOption
      requestId <- headers.get("X-Request-Id").toList.flatten.headOption
      repo <- (bodyJson \ "repository" \ "full_name").asOpt[String]
    } yield HookRequest(
      requestId = requestId,
      signature = signature,
      eventType = eventType,
      body = body,
      bodyJson = bodyJson,
      repositoryName = repo
    )
  }
}
