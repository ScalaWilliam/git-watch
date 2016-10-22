package controllers

import java.nio.file.Paths
import javax.inject.{Inject, Singleton}

import play.api.Configuration
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, Controller}
import play.twirl.api.Html

import scala.concurrent.ExecutionContext

/**
  * Created by me on 18/08/2016.
  */
@Singleton
class Main @Inject()(wsClient: WSClient, configuration: Configuration)(implicit executionContext: ExecutionContext)
  extends Controller {

  def renderUrl = configuration.underlying.getString("render.url")

  def index = Action.async {
    wsClient
      .url(s"$renderUrl/index.php")
      .get()
      .map(r => Ok(Html(r.body)))
  }

  def templatesPath = configuration.underlying.getString("git.watch.templates")

  def test = Action {
    val sampleXml = s"""<?xml version="1.0" ?>
    <?xml-stylesheet type="text/xsl" href="abc.xsl"?>

    <stuff>Hello, Chaps!</stuff>"""

    Ok(RenderXML(sampleXml, Paths.get(templatesPath)))
  }
}
