package model.github

import play.api.libs.EventSource.Event
import play.api.libs.json.Json

/**
  * Created by me on 17/08/2016.
  */
object RefPush {
  def buildEvent(hookRequest: HookRequest): Option[Event] =
    if (hookRequest.eventType == EventType.PushEvent) {
      EventType.PushEvent.unapply(hookRequest.bodyJson).map { pe =>
        Event(
          id = Some(hookRequest.requestId),
          data = Json.toJson(pe.toPush).toString,
          name = Some("ref-push")
        )
      }
    } else None
}

object CompleteDataPush {
  def buildEvent(hookRequest: HookRequest): Option[Event] = {
    Some(Event(
      data = hookRequest.bodyJson.toString(),
      id = Some(hookRequest.requestId),
      name = Some(hookRequest.eventType.name)
    ))
  }
}

case class WatchingSetting(repositoryName: String, secret: Option[String]) {
  def filter(hookRequest: HookRequest): Boolean = {
    hookRequest.repositoryName == repositoryName && secret.flatMap(hookRequest.signatureValid).getOrElse(true)
  }
}
