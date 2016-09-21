package controllers

import javax.inject.{Inject, Singleton}

import play.api.libs.json.{JsArray, Json}
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, BodyParsers, Controller}
import play.api.{Configuration, Logger}

import scala.concurrent.{Await, ExecutionContext}

/**
  * Created by me on 27/08/2016.
  */
@Singleton
class InstallHooks @Inject()(wsClient: WSClient, configuration: Configuration)(implicit executionContext: ExecutionContext)
  extends Controller {

  def logger = Logger.apply(getClass)

  import concurrent.duration._

  def clientId = configuration.underlying.getString("gw.client-id")

  def clientSecret = configuration.underlying.getString("gw.client-secret")

  def githubCallback = Action {
    req =>
      //      println(req.headers)
      val code = req.getQueryString("code").get
      val resposeF = wsClient
        .url("https://github.com/login/oauth/access_token")
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
        val userUrl = "https://api.github.com/user"
        val user = Await.result(wsClient.url(userUrl).withHeaders("Authorization" -> s"token $accessToken").get(), 5.seconds)
        val reposUrl = (user.json \ "repos_url").as[String]
        val reposJson = Await.result(wsClient.url(reposUrl)
          .withQueryString("type" -> "all", "sort" -> "updated")
          .withHeaders("Authorization" -> s"token $accessToken")
          .get(), 5.seconds).json
        val reponames = (reposJson \\ "full_name").map(_.as[String]).toList
        Ok(views.html.install(reponames))
      case _ =>
        SeeOther(s"https://github.com/login/oauth/authorize?client_id=${clientId}&allow_signup=false&scope=write:repo_hook")
    }
  }

  val tehPattern = """^[A-Za-z0-9\._-]+/[A-Za-z0-9_\.-]+$""".r

  def installPost = Action(BodyParsers.parse.multipartFormData) { req =>
    val accessToken = req.session("access-token")
    val reposIds = req.body.dataParts("repo").toList
    val repoId = reposIds.collectFirst { case x@tehPattern() => x }.get
    val userUrl = "https://api.github.com/user"
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
//    SeeOther(routes.Main.index.url).flashing("message" -> s"Repo $repoId was set up!")
//    Ok(reposIds.toString() + reposJson.toString())
    Ok(s"""Repo <code>$repoId</code> was set up! <a href="/">Homepage</a>""")
  }

}
