package controllers

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import akka.stream.scaladsl.Source
import model.github._
import play.api.inject.ApplicationLifecycle
import play.api.libs.EventSource.Event
import play.api.libs.iteratee.Concurrent
import play.api.libs.json.{JsObject, Json}
import play.api.libs.streams.Streams
import play.api.mvc.{Action, BodyParsers, Controller, RequestHeader}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by me on 31/07/2016.
  */

@Singleton
class GitHub @Inject()(applicationLifecycle: ApplicationLifecycle)(implicit actorSystem: ActorSystem, executionContext: ExecutionContext) extends Controller {

  val (enum, channel) = Concurrent.broadcast[Either[Unit, HookRequest]]

  import concurrent.duration._

  val keepAlive = actorSystem.scheduler.schedule(1.second, 5.seconds) {
    channel.push(Left(()))
  }

  applicationLifecycle.addStopHook(() => Future.successful(keepAlive.cancel()))

  def source = Source.fromPublisher(Streams.enumeratorToPublisher(enum))

  def push = Action { request =>
    val bodyText = request.body.asText.get
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
    GitHub.buildWatcher(
      fullRepo = s"$owner/$repo",
      requestHeader = rq
    ) match {
      case watcher =>
        val dataSource: Source[Event, _] = {
          source.expand {
            case Left(v) => Iterator(Event(""))
            case Right(hr) => watcher(hr).toIterator
          }
        }
        Ok.chunked(content = dataSource).as("text/event-stream")
    }
  }
}

object GitHub {
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




