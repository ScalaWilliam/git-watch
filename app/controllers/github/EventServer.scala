package controllers.github

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import akka.stream.scaladsl.Source
import model.github._
import play.api.Logger
import play.api.inject.ApplicationLifecycle
import play.api.libs.EventSource.Event
import play.api.libs.iteratee.Concurrent
import play.api.libs.json.{JsObject, Json}
import play.api.libs.streams.Streams
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import concurrent.duration._

import akka.stream.scaladsl._

/**
  * Created by me on 31/07/2016.
  */
@Singleton
class EventServer @Inject()(applicationLifecycle: ApplicationLifecycle)
                           (implicit actorSystem: ActorSystem,
                            executionContext: ExecutionContext) extends Controller {

  val (enum, channel) = Concurrent.broadcast[HookRequest]
  val (newEnum, newChannel) = Concurrent.broadcast[PushEvent]

  def source = Source.fromPublisher(Streams.enumeratorToPublisher(enum))

  def sourceAll = Source.fromPublisher(Streams.enumeratorToPublisher(newEnum))

  def allEvents() = Action {
    val dataSource: Source[Event, _] = {
      sourceAll.map { hr =>
        Event(
          id = None,
          name = Some("push"),
          data = hr.repositoryUrl
        )
      }.merge(EventServer.keepAliveSource)
    }
    Ok.chunked(content = dataSource).as("text/event-stream")
  }

  def push = Action(PushEvent.combinedParser) { request =>
    PushEvent.AtRequest(request).anyEvent.foreach { extractEvent =>
      newChannel.push(extractEvent)
    }
    val jsonBody = request.body
    val hr = HookRequest.extract(
      headers = request.headers.toMap.mapValues(_.toList),
      body = jsonBody.toString,
      bodyJson = jsonBody.asInstanceOf[JsObject]
    ).get
    channel.push(hr)
    Ok("Got it, thanks.")
  }

  def watch(owner: String, repo: String) = Action { rq =>
    Logger.info(s"Watching by ${rq.remoteAddress} (${rq.headers.get("User-Agent")}): $owner/$repo")

    val watcher = EventServer.buildWatcher(
      fullRepo = s"$owner/$repo",
      requestHeader = rq
    )
    Ok.chunked(content = source.mapConcat(watcher).merge(EventServer.keepAliveSource)).as("text/event-stream")
  }

  private implicit val eventSerializer = Json.writes[Event]

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

  def buildWatcher(fullRepo: String, requestHeader: RequestHeader): HookRequest => List[Event] = {
    val ws = WatchingSetting(repositoryName = fullRepo, secret = requestHeader.getQueryString("secret"))

    { (hookRequest: HookRequest) =>
      if (ws.filter(hookRequest)) {
        (CompleteDataPush.buildEvent(hookRequest) ++ RefPush.buildEvent(hookRequest)).toList
      }
      else List.empty
    }
  }

}




