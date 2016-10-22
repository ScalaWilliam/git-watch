package services.github
import play.api.mvc.Result
import play.api.mvc.Results._

import scala.concurrent.Future

/**
  * Created by me on 22/10/2016.
  */
class DevInstallation extends Installation {
  override def callbackToToken(code: String): Future[String] = Future.successful("good")

  override def authorizeResult: Result =
    SeeOther(controllers.routes.InstallHooks.installGet().url).withSession("access-token" -> "good")

  override def repoNames(accessToken: String): Future[List[String]] =
    Future.successful(List("ScalaWilliam/git-watch", "ScalaWilliam/git-work-content"))

  override def installTo(accessToken: String, repoId: String): Future[Unit] = Future.successful(())
}
