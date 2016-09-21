package controllers

import javax.inject.{Inject, Singleton}

import akka.agent.Agent
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, Controller}
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by me on 18/08/2016.
  */
@Singleton
class Main @Inject()(wsClient: WSClient)(implicit executionContext: ExecutionContext)
  extends Controller {
  val homepage = Agent(Option.empty[(String, String)])

  def q = controllers.GitHub.buildWatcher _

  /**
    * Fastly seems to cache even the If-None-Match stuff, ie it's not invalidating properly
    * per leaf node...
    */
  def theUrl =
  s"""https://gist.githubusercontent.com/ScalaWilliam/7c29a47e081a537605eef430a25f02b1/raw/git.watch.html?ignore=${scala.util.Random.nextInt()}"""

  def fetchPage(caching: Boolean): Future[String] = {
    homepage.get() match {
      case Some((etag, content)) if caching =>
        wsClient.url(theUrl).withHeaders("If-None-Match" -> etag).get.map { resp =>
          if (resp.status == 304) content
          else {
            val etag = resp.header("ETag").get
            val content = resp.body
            homepage.send(Some(etag -> content))
            content
          }
        }
      case _ =>
        wsClient.url(theUrl).get().map { response =>
          val etag = response.header("ETag").get
          val content = response.body
          homepage.send(Some(etag -> content))
          content
        }
    }
  }

  def makeNoCache = Action { req =>
    Ok("Good").withSession("no-cache" -> "true")
  }

  def index = Action.async { req =>
    fetchPage(caching = req.headers.get("cache").contains("false") || !req.session.get("no-cache").contains("true")).map { content =>
      Ok(Html(content))
    }
  }
}
