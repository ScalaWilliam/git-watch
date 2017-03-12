package controllers

import javax.inject.{ Inject, Singleton }

import lib.ContentPath
import play.api.http.FileMimeTypes
import play.api.mvc.{ AbstractController, ControllerComponents }

import scala.concurrent.ExecutionContext

@Singleton
class Index @Inject()(
    contentPath: ContentPath,
    components: ControllerComponents
)(implicit executionContext: ExecutionContext, fileMimeTypes: FileMimeTypes)
    extends AbstractController(components) {

  def index = Action {
    Ok.sendPath(contentPath.contentPath.resolve("index.html"))
  }

}
