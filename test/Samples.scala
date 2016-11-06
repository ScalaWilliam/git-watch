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

  /**
    * Read the sample resources: first a bunch of key-value pairs for headers, an empty line and then a body
    */
  def parseSample(source: Source): HookRequest = {
    val headers = source.getLines()
      .takeWhile(_ != "")
      .map(_.split(": "))
      .map {
        case Array(headerName, headerValue) => headerName -> headerValue
      }
      .toList
      .groupBy { case (header, value) => header }
      .mapValues(_.map { case (header, value) => value })
    val body = source.getLines().next()
    val jsonBody = Json.parse(body).asInstanceOf[JsObject]
    HookRequest.extract(
      headers = headers,
      body = body,
      bodyJson = jsonBody
    ).getOrElse {
      throw new RuntimeException(s"Failed to extract hook request from source.")
    }
  }

  def pingSample = parseResource(getClass.getResource("/sample-ping.txt"))

  def pushSample = parseResource(getClass.getResource("/sample-push.txt"))

}
