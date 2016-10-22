package controllers

import java.nio.file.Paths
import javax.inject.{Inject, Singleton}

import play.api.libs.json.{JsArray, Json}
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, BodyParsers, Controller}
import play.api.{Configuration, Logger}
import play.twirl.api.Html

import scala.concurrent.{Await, ExecutionContext, Future}

/**
  * Created by me on 27/08/2016.
  */
@Singleton
class InstallHooks @Inject()(wsClient: WSClient, configuration: Configuration)(implicit executionContext: ExecutionContext)
  extends Controller {

  def logger = Logger.apply(getClass)

  import concurrent.duration._

  def clientId = configuration.underlying.getString("gw.client-id")

  def githubUrl = configuration.getString("github.url").getOrElse("https://github.com")

  def githubApiUrl = configuration.getString("github.api-url").getOrElse("https://api.github.com")

  def clientSecret = configuration.underlying.getString("gw.client-secret")

  def templatesPath = Paths.get(configuration.underlying.getString("git.watch.templates"))

  def githubCallback = Action {
    req =>
      val code = req.getQueryString("code").get
      val resposeF = wsClient
        .url(s"$githubUrl/login/oauth/access_token")
        .withHeaders("Accept" -> "application/json")
        .post(Map(
          "client_id" -> Seq(clientId),
          "code" -> Seq(code),
          "client_secret" -> Seq(clientSecret)
        ))

      val response = Await.result(resposeF, 5.seconds)
      val accessToken = (response.json \ "access_token").as[String]
      SeeOther(routes.InstallHooks.installGet.url).withSession("access-token" -> accessToken)
  }

  def installGet = Action { req =>
    req.session.get("access-token") match {
      case Some(accessToken) =>
        val userUrl = s"$githubApiUrl/user"
        val user = Await.result(wsClient.url(userUrl).withHeaders("Authorization" -> s"token $accessToken").get(), 5.seconds)
        val reposUrl = (user.json \ "repos_url").as[String]
        val reposJson = Await.result(wsClient.url(reposUrl)
          .withQueryString("type" -> "all", "sort" -> "updated")
          .withHeaders("Authorization" -> s"token $accessToken")
          .get(), 5.seconds).json
        val reponames = (reposJson \\ "full_name").map(_.as[String]).toList
        Ok(Html(RenderXML(
          """<?xml-stylesheet type="text/xsl" href="id.xsl"?>
          """ +
            installXml(reponames).toString, templatesPath)))

      case _ =>
        SeeOther(s"$githubUrl/login/oauth/authorize?client_id=${clientId}&allow_signup=false&scope=write:repo_hook")
    }
  }

  def idRender = """<?xml-stylesheet type="text/xsl" href="id.xsl"?>"""

  val tehPattern = """^[A-Za-z0-9\._-]+/[A-Za-z0-9_\.-]+$""".r

  def installPost = Action(BodyParsers.parse.multipartFormData) { req =>
    val accessToken = req.session("access-token")
    val reposIds = req.body.dataParts("repo").toList
    val repoId = reposIds.collectFirst { case x@tehPattern() => x }.get
    val userUrl = s"${githubApiUrl}/user"
    val user = Await.result(wsClient.url(userUrl)
      .withQueryString("type" -> "all", "sort" -> "updated")
      .withHeaders("Authorization" -> s"token $accessToken")
      .get(), 5.seconds)
    val reposUrl = (user.json \ "repos_url").as[String]
    val reposJson = Await.result(wsClient.url(reposUrl).withHeaders("Authorization" -> s"token $accessToken").get(), 5.seconds).json
    val reposList = reposJson.as[JsArray].value.toList
    val hookUrl = reposList.filter(i => (i \ "full_name").as[String] == repoId).map(i => i \ "hooks_url").map(_.as[String]).head
    val json = """{"name": "web", "active": true, "events": [ "*"],  "config": {    "url": "https://git.watch/github/",   "content_type": "json"  }}"""
    val resX = Await.result(wsClient.url(hookUrl)
      .withHeaders("Authorization" -> s"token $accessToken")
      .post(Json.parse(json)), 5.seconds)
    logger.info(s"user = ${user.json \ "login"} repo id = ${repoId}, accessToken = ${accessToken}, hookUrl = ${hookUrl}, result = ${resX}")


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
