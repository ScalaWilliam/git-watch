package controllers

import javax.inject.{ Inject, Singleton }

import akka.actor.ActorSystem
import akka.stream.scaladsl.{ Source, _ }
import model.PushEvent
import play.api.{ Configuration, Logger }
import play.api.libs.EventSource.Event
import play.api.libs.iteratee.Concurrent
import play.api.libs.iteratee.streams.IterateeStreams
import play.api.libs.json.JsValue
import play.api.mvc._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/**
  * Created by me on 31/07/2016.
  */
@Singleton
class EventServer(validateIp: Boolean)(implicit actorSystem: ActorSystem,
                                       components: ControllerComponents,
                                       executionContext: ExecutionContext)
    extends AbstractController(components) {

  Logger.info(s"Validating IPs: ${validateIp}")

  @Inject
  def this(configuration: Configuration)(implicit actorSystem: ActorSystem,
                                         components: ControllerComponents,
                                         executionContext: ExecutionContext) = {
    this(validateIp = configuration.get[Boolean]("validate-ip"))
  }

  private val (enumerator, channel) = Concurrent.broadcast[PushEvent]

  private def pushEvents =
    Source.fromPublisher(IterateeStreams.enumeratorToPublisher(enumerator))

  def push(tag: String): Action[JsValue] =
    Action(PushEvent.combinedParser(parse)) { request =>
      val atRequest = {
        if (validateIp)
          PushEvent.AtIpValidatedRequest(request)
        else
          PushEvent.AtRequest(request)
      }
      atRequest.anyEvent.foreach { extractEvent =>
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

  def eventStreamWebsocket: WebSocket = WebSocket.accept[String, String] { rq =>
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
