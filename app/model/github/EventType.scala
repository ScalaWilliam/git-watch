package model.github

import play.api.libs.json.JsObject

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
  }

  case object PingEvent extends EventType {
    override def name: String = "ping"
  }

  case class OtherEvent(name: String) extends EventType

  def unapply(str: String): Option[EventType] = {
    str match {
      case "push" => Some(PushEvent)
      case "ping" => Some(PingEvent)
      case other => Some(OtherEvent(other))
    }
  }

}
