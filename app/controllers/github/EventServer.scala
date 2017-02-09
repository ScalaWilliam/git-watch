package controllers.github

import java.time.format.DateTimeFormatter
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

/**
  * Created by me on 31/07/2016.
  */
@Singleton
class EventServer @Inject()(applicationLifecycle: ApplicationLifecycle)
                           (implicit actorSystem: ActorSystem,
                            executionContext: ExecutionContext) extends Controller {

  Logger.info("Initialized EventServer...")

  val (enum, channel) = Concurrent.broadcast[Either[Unit, HookRequest]]
  val (newEnum, newChannel) = Concurrent.broadcast[Either[Unit, ExtractEvent]]

  val keepAlive = actorSystem.scheduler.schedule(1.second, 10.seconds) {
    channel.push(Left(()))
    newChannel.push(Left(()))
  }

  applicationLifecycle.addStopHook(() => Future.successful(keepAlive.cancel()))

  def source = Source.fromPublisher(Streams.enumeratorToPublisher(enum))

  def sourceAll = Source.fromPublisher(Streams.enumeratorToPublisher(newEnum))

  def allEvents() = Action {
    val dataSource: Source[Event, _] = {
      sourceAll.expand {
        case Left(v) => Iterator(EventServer.keepAliveEvent)
        case Right(hr) => hr.repositoryUrls.map { url =>
          Event(
            id = None,
            name = Some(hr.eventType),
            data = url
          )
        }.toIterator
      }
    }
    Ok.chunked(content = dataSource).as("text/event-stream")
  }

  def push = Action(BodyParsers.parse.tolerantText) { request =>
    ExtractEvent.fromJsonRequest(request.map(Json.parse)).foreach { extractEvent =>
      newChannel.push(Right(extractEvent))
    }
    val bodyText = request.body
    val jsonBody = Json.parse(bodyText)
    val hr = HookRequest.extract(
      headers = request.headers.toMap.mapValues(_.toList),
      body = bodyText,
      bodyJson = jsonBody.asInstanceOf[JsObject]
    ).get
    channel.push(Right(hr))
    Ok("Got it, thanks.")
  }

  def watch(owner: String, repo: String) = Action { rq =>
    Logger.info(s"Watching by ${rq.remoteAddress} (${rq.headers.get("User-Agent")}): $owner/$repo")

    EventServer.buildWatcher(
      fullRepo = s"$owner/$repo",
      requestHeader = rq
    ) match {
      case watcher =>
        val dataSource: Source[Event, _] = {
          source.expand {
            case Left(_) => Iterator(EventServer.keepAliveEvent)
            case Right(hr) => watcher(hr).toIterator
          }
        }
        Ok.chunked(content = dataSource).as("text/event-stream")
    }
  }

  private implicit val eventSerializer = Json.writes[Event]

  def watchWs(owner: String, repo: String): WebSocket = WebSocket.accept[String, String] { rq =>
    Logger.info(s"Watching WS by ${rq.remoteAddress} (${rq.headers.get("User-Agent")}): $owner/$repo")

    EventServer.buildWatcher(
      fullRepo = s"$owner/$repo",
      requestHeader = rq
    ) match {
      case watcher =>
        import akka.stream.scaladsl._
        Flow.fromSinkAndSource(
          sink = Sink.ignore,
          source = source
            .collect { case Right(r) => r }
            .mapConcat(hr => watcher(hr))
            .map(e => Json.toJson(e).toString())
        )
    }
  }
}

object EventServer {
  val keepAliveEvent = Event("")

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




