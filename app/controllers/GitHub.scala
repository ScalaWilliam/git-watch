package controllers

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import akka.stream.scaladsl.Source
import controllers.GitHub.PushParse
import play.api.data.validation.ValidationError
import play.api.inject.ApplicationLifecycle
import play.api.libs.EventSource.Event
import play.api.libs.iteratee.Concurrent
import play.api.libs.json.{JsError, JsSuccess, Json, Reads}
import play.api.libs.streams.Streams
import play.api.mvc.{Action, BodyParsers, Controller}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by me on 31/07/2016.
  */

@Singleton
class GitHub @Inject()(applicationLifecycle: ApplicationLifecycle)(implicit actorSystem: ActorSystem, executionContext: ExecutionContext) extends Controller {

  val (enum, channel) = Concurrent.broadcast[Event]

  import concurrent.duration._

  val keepAlive = actorSystem.scheduler.schedule(1.second, 5.seconds) {
    channel.push(Event(data = "", id = None, name = None))
  }

  applicationLifecycle.addStopHook(() => Future.successful(keepAlive.cancel()))

  def source = Source.fromPublisher(Streams.enumeratorToPublisher(enum))

  def push = Action(BodyParsers.parse.json) { request =>
    channel.push(Event(data = request.body.toString, id = None, name = None))
    request.body.asOpt[PushParse].foreach { pp =>
      channel.push(Event(data = pp.after, id = Some(pp.ref), name = Some("push")))
    }
    Ok("Got it, thanks.")
  }

  def watch(owner: String, repo: String) = Action {
    Ok.chunked(
      content = source
    ).as("text/event-stream")
  }
}

object GitHub {

  case class PushParse(ref: String, after: String) {
    def safe = PushParse.safeHash.findFirstIn(after).isDefined && PushParse.safeRef.findFirstIn(ref).isDefined
  }

  object PushParse {
    val safeHash = "^[a-f0-9]+$".r
    val safeRef = "^[a-zA-Z0-9/]+$".r
    implicit val reads: Reads[PushParse] = Json.reads[PushParse].filter(ValidationError("Unsafe input given"))(_.safe)
  }

}
