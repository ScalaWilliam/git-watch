package controllers

import java.nio.file.{Files, Path, Paths}
import java.util.stream.Collectors
import javax.inject.Inject

import play.Environment
import play.api.Configuration
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext

/**
  * Created by me on 18/08/2016.
  */
class StaticDev @Inject()(environment: Environment, configuration: Configuration)(implicit executionContext: ExecutionContext)
  extends Controller {

  require(!environment.isProd, s"Environment is ${environment}")

  def contentPath = Paths.get(configuration.underlying.getString("git.watch.content"))

  def staticPath = contentPath.resolve("static")

  def resources = {
    import scala.collection.JavaConverters._
    Files.list(staticPath).collect(Collectors.toList[Path]).asScala.toList
  }

  def get(path: String) = resources.find(_.endsWith(path)) match {
    case Some(v) => Action {
      Ok.sendFile(v.toFile)
    }
    case None => Action {
      NotFound("Not found")
    }
  }
}
