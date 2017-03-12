package controllers

import java.util.Base64
import javax.inject.Inject

import play.api.libs.json.{ JsObject, JsString, Json }
import play.api.mvc.{ AbstractController, ControllerComponents }

/**
  * Provide the current git commit SHA so we can double check a deployment.
  */
class Version @Inject()(components: ControllerComponents) extends AbstractController(components) {

  def commitDescription =
    gitwatch.BuildInfo.gitCommitDescription.map { encoded =>
      new String(Base64.getDecoder.decode(encoded), "UTF-8")
    }

  def version = Action {
    val parsedJson =
      Json.parse(gitwatch.BuildInfo.toJson).asInstanceOf[JsObject]
    val two = JsObject(commitDescription.map(d => "gitCommitDescription" -> JsString(d)).toSeq)
    Ok(parsedJson ++ two)
  }

}
