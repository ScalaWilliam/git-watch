package controllers

import java.nio.file.{Files, Path}
import java.util.stream.Collectors
import javax.inject.Inject

import lib.ContentPath
import play.Environment
import play.api.http.FileMimeTypes
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext

/**
  * Serve static files for development since we don't want to run nginx on dev machine.
  */
class StaticResources @Inject()(environment: Environment,
                                contentPath: ContentPath,
                                components: ControllerComponents)(
    implicit executionContext: ExecutionContext,
    fileMimeTypes: FileMimeTypes)
    extends AbstractController(components) {

  private def staticPath = contentPath.contentPath.resolve("static")

  private def resources = {
    Files.list(staticPath).collect(Collectors.toList[Path]).asScala.toList
  }

  def get(path: String) = resources.find(_.endsWith(path)) match {
    case Some(v) =>
      Action {
        Ok.sendFile(v.toFile)
      }
    case None =>
      Action {
        NotFound("Not found")
      }
  }
}
