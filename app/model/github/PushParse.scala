package model.github

import play.api.data.validation.ValidationError
import play.api.libs.json.{Json, Reads}

/**
  * Created by me on 17/08/2016.
  */
case class PushParse(ref: String, after: String) {
  def isSafe: Boolean = PushParse.safeHash.findFirstIn(after).isDefined && PushParse.safeRef.findFirstIn(ref).isDefined

  def toPush: RefPushEvent = RefPushEvent(ref, after)
}

object PushParse {
  val safeHash = "^[a-f0-9]+$".r
  val safeRef = "^[a-zA-Z0-9/]+$".r
  implicit val reads: Reads[PushParse] = Json.reads[PushParse].filter(ValidationError("Unsafe input given"))(_.isSafe)
}
