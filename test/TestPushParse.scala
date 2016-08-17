
import org.apache.commons.codec.digest.HmacUtils
import org.scalatest.{FunSuite, Matchers}
import play.api.libs.json.{JsObject, Json}

import scala.io.Source

/**
  * Created by me on 31/07/2016.
  */
class TestPushParse extends FunSuite with Matchers {

  //  test("It works") {
  //    val sampleJson = """{"ref":"refs/heads/master","before":"c04b0fb8d488e198eab72dc1ff4e619deb30a284","after":"a8b8c587f7dc2750328530b3a0434edeedffca83","created":false,"deleted"}"""
  //    val json = Json.parse(sampleJson)
  //    json.as[PushParse].ref shouldBe "refs/heads/master"
  //  }

  lazy val pingSample = HookRequest.parseSample(scala.io.Source.fromURL(getClass.getResource("/sample-ping.txt")))
  lazy val pushSample = HookRequest.parseSample(scala.io.Source.fromURL(getClass.getResource("/sample-push.txt")))

  test("Sample parses") {
    pingSample
    pushSample
    info(s"Push signature = ${pushSample.signature}")
    pushSample.repositoryName shouldBe "AptElements/git-watch"
  }

  test("Using a secret works") {
    pushSample.signatureValid("test").get shouldBe true
    pushSample.signatureValid("testX").get shouldBe false
  }

  test("Applying the 'deploy to x branch' preset works") {

  }

}

sealed trait EventType {
  def name: String
}

object EventType {

  case object PushEvent extends EventType {
    override def name: String = "push"
  }

  case object PingEvent extends EventType {
    override def name: String = "ping"
  }

  case class OtherEvent(name: String) extends EventType

  def unapply(str: String): Option[EventType] = {
    str match {
      case "push" => Some(PushEvent)
      case "ping" => Some(PingEvent)
      case other => Some(OtherEvent(other))
    }
  }

}

case class HookRequest(signature: Option[String], eventType: EventType, body: String, bodyJson: JsObject, repositoryName: String) {
  def signatureValid(secret: String): Option[Boolean] = {
    signature.map {
      case HookRequest.parseSignature(sv) =>
        val resSignature = HmacUtils.hmacSha1Hex(secret, body)
        sv == resSignature
      case _ => false
    }
  }
}

object HookRequest {
  val parseSignature = """^sha1=(.*)$""".r

  def extract(headers: Map[String, List[String]], body: String, bodyJson: JsObject): Option[HookRequest] = {
    for {
      eventType <- headers.get("X-Github-Event").toList.flatMap(_.flatMap(EventType.unapply)).headOption
      signature = headers.get("X-Hub-Signature").toList.flatten.headOption
      repo <- (bodyJson \ "repository" \ "full_name").asOpt[String]
    } yield HookRequest(
      signature = signature,
      eventType = eventType,
      body = body,
      bodyJson = bodyJson,
      repositoryName = repo
    )
  }

  def parseSample(source: Source): HookRequest = {
    val headers = source.getLines().takeWhile(_ != "").map(_.split(": ")).map { case Array(h, v) =>
      h -> v
    }.toList.groupBy(_._1).mapValues(_.map(_._2))
    val body = source.getLines().next()
    val jsonBody = Json.parse(body).asInstanceOf[JsObject]
    HookRequest.extract(headers = headers, body = body, bodyJson = jsonBody).get
  }
}
