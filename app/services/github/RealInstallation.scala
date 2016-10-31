package services.github

import javax.inject.Inject

import play.api.libs.json.{JsArray, JsValue, Json}
import play.api.libs.ws.WSClient
import play.api.mvc.Result
import play.api.mvc.Results._
import play.api.{Configuration, Logger}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal
import scala.async.Async.{async, await}

/**
  * Created by me on 22/10/2016.
  */
class RealInstallation @Inject()(wsClient: WSClient, configuration: Configuration)
                                (implicit executionContext: ExecutionContext) extends Installation {

  protected def logger = Logger.apply(getClass)

  protected def githubApiUrl = configuration.getString("github.api-url").getOrElse("https://api.github.com")

  protected def githubUrl = configuration.getString("github.url").getOrElse("https://github.com")

  protected def clientId = configuration.underlying.getString("gw.client-id")

  protected def clientSecret = configuration.underlying.getString("gw.client-secret")

  protected def authorizeUrl = s"$githubUrl/login/oauth/authorize?client_id=${clientId}&allow_signup=false&scope=write:repo_hook"

  protected def hookJson: JsValue = Json.parse {
    """{"name": "web", "active": true, "events": [ "*"],  "config": {    "url": "https://git.watch/github/",   "content_type": "json"  }}"""
  }

  override def callbackToToken(code: String): Future[String] = async {
    val response = await {
      wsClient
        .url(s"$githubUrl/login/oauth/access_token")
        .withHeaders("Accept" -> "application/json")
        .post(Map(
          "client_id" -> Seq(clientId),
          "code" -> Seq(code),
          "client_secret" -> Seq(clientSecret)
        ))
    }
    try {
      (response.json \ "access_token").as[String]
    }
    catch {
      case NonFatal(e) =>
        throw new RuntimeException(s"Failed to find an access token for response ${response} due to $e", e)
    }

  }

  override def authorizeResult: Result = {
    SeeOther(authorizeUrl)
  }

  override def repoNames(accessToken: String): Future[List[String]] = async {
    val userUrl = s"$githubApiUrl/user"
    val user = await(wsClient.url(userUrl).withHeaders("Authorization" -> s"token $accessToken").get())
    val reposUrl = (user.json \ "repos_url").as[String]
    val repos = await {
      wsClient.url(reposUrl)
        .withQueryString("type" -> "all", "sort" -> "updated")
        .withHeaders("Authorization" -> s"token $accessToken")
        .get()
    }
    (repos.json \\ "full_name").map(_.as[String]).toList
  }

  override def installTo(accessToken: String, repoId: String): Future[Unit] = async {
    val userUrl = s"${githubApiUrl}/user"
    val user = await {
      wsClient.url(userUrl)
        .withQueryString("type" -> "all", "sort" -> "updated")
        .withHeaders("Authorization" -> s"token $accessToken")
        .get()
    }
    val reposUrl = (user.json \ "repos_url").as[String]
    val repos = await {
      wsClient.url(reposUrl).withHeaders("Authorization" -> s"token $accessToken").get()
    }
    val reposList = repos.json.as[JsArray].value.toList
    val hookUrl = reposList.filter(i => (i \ "full_name").as[String] == repoId).map(i => i \ "hooks_url").map(_.as[String]).head
    val resX = await {
      wsClient.url(hookUrl)
        .withHeaders("Authorization" -> s"token $accessToken")
        .post(hookJson)
    }
    logger.info(s"user = ${user.json \ "login"} repo id = ${repoId}, accessToken = ${accessToken}, hookUrl = ${hookUrl}, result = ${resX}")
  }

}
