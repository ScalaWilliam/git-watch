package controllers.github

import java.nio.file.Paths
import javax.inject.{Inject, Singleton}

import controllers.github.InstallHooks._
import org.jsoup.Jsoup
import play.api.Configuration
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, BodyParsers, Controller}
import play.twirl.api.Html
import services.github.Installation

import scala.concurrent.{Await, ExecutionContext}

/**
  * Created by me on 27/08/2016.
  */
@Singleton
class InstallHooks @Inject()(wsClient: WSClient, configuration: Configuration,
                             installation: Installation)(implicit executionContext: ExecutionContext)
  extends Controller {

  import concurrent.duration._

  def contentPath = Paths.get(configuration.underlying.getString("git.watch.content"))

  def githubCallback = Action {
    req =>
      val code = req.getQueryString("code").get
      val accessToken = Await.result(installation.callbackToToken(code), 5.seconds)
      SeeOther(routes.InstallHooks.installGet().url).withSession("access-token" -> accessToken)
  }

  def installGet = Action { req =>
    req.session.get("access-token") match {
      case Some(accessToken) =>
        val reponames = Await.result(installation.repoNames(accessToken), 5.seconds)
        val doc = Jsoup.parse(contentPath.resolve("repos.html").toFile, "UTF-8")
        val options = doc.select("option")
        val firstOption = options.first()
        reponames.map { name =>
          val opt = firstOption.clone()
          opt.text(name).attr("value", name)
          opt
        }.foreach(firstOption.parent().appendChild)
        options.remove()
        Ok(Html(doc.html()))
      case _ =>
        installation.authorizeResult
    }
  }

  def installPost = Action(BodyParsers.parse.multipartFormData) { req =>
    val accessToken = req.session("access-token")
    val reposIds = req.body.dataParts("repo").toList
    val repoId = reposIds.collectFirst { case x@validRepoPattern() => x }.get
    Await.result(installation.installTo(accessToken, repoId), 5.seconds)

    val doc = Jsoup.parse(contentPath.resolve("repo-setup.html").toFile, "UTF-8")
    import collection.JavaConverters._
    doc.select("repo-name").asScala.foreach { el => el.text(repoId) }
    doc.select("textarea").asScala.foreach { ta => ta.text(ta.text().replaceAllLiterally("{{repo-name}}", repoId)) }
    Ok(Html(doc.html()))
  }


}

object InstallHooks {

  val validRepoPattern = """^[A-Za-z0-9\._-]+/[A-Za-z0-9_\.-]+$""".r
}
