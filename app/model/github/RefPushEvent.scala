package model.github

import play.api.libs.json.{Json, Writes}

/**
  * Created by me on 18/08/2016.
  */
object RefPushEvent {
  implicit val writes: Writes[RefPushEvent] = Json.writes[RefPushEvent]
}

case class RefPushEvent(ref: String, commit: String) {

}
