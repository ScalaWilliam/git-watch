package controllers

import java.nio.file.Paths
import javax.inject.{Inject, Singleton}

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

  def templatesPath = Paths.get(configuration.underlying.getString("git.watch.templates"))

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
        Ok(Html(RenderXML(
          """<?xml-stylesheet type="text/xsl" href="id.xsl"?>
          """ +
            installXml(reponames).toString, templatesPath)))

      case _ =>
        installation.authorizeResult
    }
  }

  def idRender = """<?xml-stylesheet type="text/xsl" href="id.xsl"?>"""

  val tehPattern = """^[A-Za-z0-9\._-]+/[A-Za-z0-9_\.-]+$""".r

  def installPost = Action(BodyParsers.parse.multipartFormData) { req =>
    val accessToken = req.session("access-token")
    val reposIds = req.body.dataParts("repo").toList
    val repoId = reposIds.collectFirst { case x@tehPattern() => x }.get
    Await.result(installation.installTo(accessToken, repoId), 5.seconds)

    val inXml = <p>Repo
      <code>
        {repoId}
      </code>
      was set up!
      <a href="/">Homepage</a>
    </p>
    Ok(Html(RenderXML(idRender + inXml.toString(), templatesPath)))
  }

  def installXml(reponames: List[String]): scala.xml.Elem = <html>
    <head>
      <title>Git Watch</title>
      <link rel="stylesheet" href="/static/main.css" type="text/css"/>
    </head>
    <body>
      <h1>Git Watch</h1>
      <h2>Install</h2>
      <form name="submitter" method="post" enctype="multipart/form-data">
        <button type="submit">Set up git.watch webhooks</button>
        <br/>
        <select name="repo" size="20">
          {reponames.map { rn =>
          <option value={rn}>
            {rn}
          </option>
        }}
        </select>
        <hr/>
        Other repo:
        <input type="text" name="repo" pattern="[A-Za-z0-9_-]+/[A-Za-z0-9_-]+/"
               placeholder="full repository name, eg AptElements/git-watch"/>
      </form>
    </body>
  </html>

}
