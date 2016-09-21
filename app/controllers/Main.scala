package controllers

import java.nio.file.Paths
import javax.inject.{Inject, Singleton}

import play.api.libs.ws.WSClient
import play.api.mvc.{Action, Controller}
import play.api.{Environment, Mode}
import play.twirl.api.Html

import scala.concurrent.ExecutionContext

/**
  * Created by me on 18/08/2016.
  */
@Singleton
class Main @Inject()(wsClient: WSClient, environment: Environment)(implicit executionContext: ExecutionContext)
  extends Controller {

  def templatesPath = if (environment.mode == Mode.Prod) Paths.get("templates") else Paths.get("dist", "templates")

  def indexPath = templatesPath.resolve("index.html")

  def index = Action {
    val src = scala.io.Source.fromFile(indexPath.toFile)
    try Ok(Html(src.mkString))
    finally src.close()
  }
}
