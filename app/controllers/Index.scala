package controllers

import javax.inject.{Inject, Singleton}

import lib.ContentPath
import play.api.mvc.{Action, Controller}

/**
  * Created by me on 18/08/2016.
  */
@Singleton
class Index @Inject()(contentPath: ContentPath) extends Controller {

  def index = Action {
    Ok.sendPath(contentPath.contentPath.resolve("index.html"))
  }

}
