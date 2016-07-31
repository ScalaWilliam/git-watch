package controllers

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import akka.stream.scaladsl.Source
import play.api.inject.ApplicationLifecycle
import play.api.libs.EventSource.Event
import play.api.libs.iteratee.Concurrent
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
    Ok("Got it, thanks.")
  }

  def watch(owner: String, repo: String) = Action {
    Ok.chunked(
      content = source
    ).as("text/event-stream")
  }
}
