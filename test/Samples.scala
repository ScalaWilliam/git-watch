import java.net.URL

import model.github.HookRequest
import play.api.libs.json.{JsObject, Json}

import scala.io.Source

/**
  * Created by me on 18/08/2016.
  */
trait Samples {
  def pingSample: HookRequest

  def pushSample: HookRequest
}

object Samples {
  def parseResource(uRL: URL): HookRequest = {
    val source = scala.io.Source.fromURL(uRL)
    try parseSample(source)
    finally source.close()
  }

  def parseSample(source: Source): HookRequest = {
    val headers = source.getLines().takeWhile(_ != "").map(_.split(": ")).map { case Array(h, v) =>
      h -> v
    }.toList.groupBy(_._1).mapValues(_.map(_._2))
    val body = source.getLines().next()
    val jsonBody = Json.parse(body).asInstanceOf[JsObject]
    HookRequest.extract(headers = headers, body = body, bodyJson = jsonBody).get
  }


  def pingSample = parseResource(getClass.getResource("/sample-ping.txt"))

  def pushSample = parseResource(getClass.getResource("/sample-push.txt"))

}
