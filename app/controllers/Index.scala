package controllers

import java.nio.file.Paths
import javax.inject.{Inject, Singleton}

import play.api.Configuration
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext

/**
  * Created by me on 18/08/2016.
  */
@Singleton
class Index @Inject()(wsClient: WSClient, configuration: Configuration)(implicit executionContext: ExecutionContext)
  extends Controller {

  def index = Action {
    Ok.sendPath(contentPath.resolve("index.html"))
  }

  def contentPath = Paths.get(configuration.underlying.getString("git.watch.content"))

}
