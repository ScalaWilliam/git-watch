package services.github

import javax.inject.Inject

import play.api.libs.json.{JsArray, Json}
import play.api.libs.ws.WSClient
import play.api.mvc.Result
import play.api.mvc.Results._
import play.api.{Configuration, Logger}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

/**
  * Created by me on 22/10/2016.
  */
class RealInstallation @Inject()(wsClient: WSClient, configuration: Configuration)
                                (implicit executionContext: ExecutionContext) extends Installation {
  def githubUrl = configuration.getString("github.url").getOrElse("https://github.com")

  def clientId = configuration.underlying.getString("gw.client-id")

  def clientSecret = configuration.underlying.getString("gw.client-secret")

  override def callbackToToken(code: String): Future[String] = {
    val resposeF = wsClient
      .url(s"$githubUrl/login/oauth/access_token")
      .withHeaders("Accept" -> "application/json")
      .post(Map(
        "client_id" -> Seq(clientId),
        "code" -> Seq(code),
        "client_secret" -> Seq(clientSecret)
      ))

    resposeF.map { response => (response.json \ "access_token").as[String]
    }

  }

  override def authorizeResult: Result = {
    val url = s"$githubUrl/login/oauth/authorize?client_id=${clientId}&allow_signup=false&scope=write:repo_hook"
    SeeOther(url)
  }

  def logger = Logger.apply(getClass)

  def githubApiUrl = configuration.getString("github.api-url").getOrElse("https://api.github.com")

  override def repoNames(accessToken: String): Future[List[String]] = {
    val userUrl = s"$githubApiUrl/user"
    val user = Await.result(wsClient.url(userUrl).withHeaders("Authorization" -> s"token $accessToken").get(), 5.seconds)
    val reposUrl = (user.json \ "repos_url").as[String]
    val reposJson = wsClient.url(reposUrl)
      .withQueryString("type" -> "all", "sort" -> "updated")
      .withHeaders("Authorization" -> s"token $accessToken")
      .get().map(_.json)
    reposJson.map(j => (j \\ "full_name").map(_.as[String]).toList)
  }

  override def installTo(accessToken: String, repoId: String): Future[Unit] = {
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
    wsClient.url(hookUrl)
      .withHeaders("Authorization" -> s"token $accessToken")
      .post(Json.parse(json)).map { resX =>
      logger.info(s"user = ${user.json \ "login"} repo id = ${repoId}, accessToken = ${accessToken}, hookUrl = ${hookUrl}, result = ${resX}")
      resX
    }
  }
}
