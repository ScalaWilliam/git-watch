package controllers

import javax.inject.{ Inject, Singleton }

import akka.actor.{ ActorSystem, Cancellable }
import akka.stream.scaladsl.{ Source, _ }
import model.PushEvent
import model.PushEvent.PushEventBuilder
import mvc.CombinedJsonBodyParser
import play.api.Configuration
import play.api.libs.EventSource.Event
import play.api.libs.iteratee.Concurrent
import play.api.libs.iteratee.streams.IterateeStreams
import play.api.libs.json.JsValue
import play.api.mvc._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/**
  * Event server: we take events, push them to a broadcast channel and then push them to any EventSource listeners.
  */
@Singleton
class EventServer(atRequestBuild: PushEventBuilder)(implicit actorSystem: ActorSystem,
                                                    components: ControllerComponents,
                                                    executionContext: ExecutionContext)
    extends AbstractController(components) {

  @Inject
  def this(configuration: Configuration)(implicit actorSystem: ActorSystem,
                                         components: ControllerComponents,
                                         executionContext: ExecutionContext) = {
    this(PushEvent.builder(validateIp = configuration.get[Boolean]("validate-ip")))
  }

  private val (enumerator, channel) = Concurrent.broadcast[PushEvent]

  private def pushEvents =
    Source.fromPublisher(IterateeStreams.enumeratorToPublisher(enumerator))

  def push(tag: String): Action[JsValue] =
    Action(CombinedJsonBodyParser(parse)) { request =>
      atRequestBuild(request).foreach { extractEvent =>
        channel.push(extractEvent)
      }
      Ok("Got it, thanks.")
    }

  def eventStream() = Action {
    Ok.chunked(content = pushEvents.map(_.toEventSource).merge(EventServer.keepAliveEventSource))
      .as("text/event-stream")
  }

  /**
    * This is just for testing. I'll keep this for now if there's a use case for it.
    */
  def eventStreamWebSocket: WebSocket = WebSocket.accept[String, String] { rq =>
    Flow.fromSinkAndSource(
      sink = Sink.ignore,
      source = pushEvents.map(e => e.repositoryUrl)
    )
  }
}

object EventServer {

  /** Needed to prevent premature close of connection if not enough events coming through **/
  val keepAliveEventSource: Source[Event, Cancellable] = {
    Source.tick(10.seconds, 10.seconds, Event(""))
  }
}
