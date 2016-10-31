package services.github

import play.api.mvc.Result

import scala.concurrent.Future

/**
  * Created by me on 22/10/2016.
  */
trait Installation {
  def callbackToToken(code: String): Future[String]

  def authorizeResult: Result

  def repoNames(accessToken: String): Future[List[String]]

  def installTo(accessToken: String, repoId: String): Future[Unit]
}
