package model.github

import play.api.data.validation.ValidationError
import play.api.libs.json.{JsObject, Json, Reads, Writes}

/**
  * Created by me on 17/08/2016.
  */
sealed trait EventType {
  def name: String
}

object EventType {

  case object PushEvent extends EventType {
    override def name: String = "push"

    def unapply(bodyJson: JsObject): Option[PushParse] = {
      bodyJson.asOpt[PushParse]
    }

    case class PushParse(ref: String, after: String) {
      def isSafe: Boolean = PushParse.safeHash.findFirstIn(after).isDefined && PushParse.safeRef.findFirstIn(ref).isDefined

      def toPush: RefPushEvent = RefPushEvent(ref, after)
    }

    object PushParse {
      val safeHash = "^[a-f0-9]+$".r
      val safeRef = "^[a-zA-Z0-9/]+$".r
      implicit val reads: Reads[PushParse] = Json.reads[PushParse].filter(ValidationError("Unsafe input given"))(_.isSafe)
    }


    object RefPushEvent {
      implicit val writes: Writes[RefPushEvent] = Json.writes[RefPushEvent]
    }

    case class RefPushEvent(ref: String, commit: String) {

    }
  }

  case class OtherEvent(name: String) extends EventType

  def unapply(str: String): Option[EventType] = {
    str match {
      case "push" => Some(PushEvent)
      case other => Some(OtherEvent(other))
    }
  }

}
