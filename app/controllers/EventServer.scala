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

  private val (newEnum, newChannel) = Concurrent.broadcast[PushEvent]

  def sourceAll = Source.fromPublisher(Streams.enumeratorToPublisher(newEnum))

  def allEvents() = Action {
    val dataSource: Source[Event, _] = {
      sourceAll
        .map { hr =>
          Event(
            id = None,
            name = Some("push"),
            data = hr.repositoryUrl
          )
        }
        .merge(EventServer.keepAliveSource)
    }
    Ok.chunked(content = dataSource).as("text/event-stream")
  }

  def push(tag: String) = Action(PushEvent.combinedParser) { request =>
    PushEvent.AtRequest(request).anyEvent.foreach { extractEvent =>
      newChannel.push(extractEvent)
    }
    Ok("Got it, thanks.")
  }

  def allEventsWs: WebSocket = WebSocket.accept[String, String] { rq =>
    Flow.fromSinkAndSource(
      sink = Sink.ignore,
      source = sourceAll.map(e => e.repositoryUrl)
    )
  }
}

object EventServer {
  val keepAliveEvent = Event("")

  val keepAliveSource = Source.tick(10.seconds, 10.seconds, keepAliveEvent)
}
