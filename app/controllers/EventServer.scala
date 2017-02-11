package controllers

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Source, _}
import model.PushEvent
import play.api.libs.EventSource.Event
import play.api.libs.iteratee.Concurrent
import play.api.libs.streams.Streams
import play.api.mvc._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/**
  * Created by me on 31/07/2016.
  */
@Singleton
class EventServer @Inject()(implicit actorSystem: ActorSystem,
                            executionContext: ExecutionContext)
    extends Controller {

  private val (enumerator, channel) = Concurrent.broadcast[PushEvent]

  private def pushEvents =
    Source.fromPublisher(Streams.enumeratorToPublisher(enumerator))

  def push(tag: String) = Action(PushEvent.combinedParser) { request =>
    PushEvent.AtRequest(request).anyEvent.foreach { extractEvent =>
      channel.push(extractEvent)
    }
    Ok("Got it, thanks.")
  }

  def eventStream() = Action {
    val events =
      pushEvents.map(_.toEventSource).merge(EventServer.keepAliveSource)
    Ok.chunked(content = events)
      .as("text/event-stream")
  }

  def eventStreamWebsocket: WebSocket = WebSocket.accept[String, String] {
    rq =>
      Flow.fromSinkAndSource(
        sink = Sink.ignore,
        source = pushEvents.map(e => e.repositoryUrl)
      )
  }
}

object EventServer {
  val keepAliveEvent = Event("")

  val keepAliveSource = Source.tick(10.seconds, 10.seconds, keepAliveEvent)
}
