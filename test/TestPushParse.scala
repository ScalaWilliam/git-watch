
import controllers.GitHub
import model.github.EventType.PushEvent
import model.github.HookRequest
import org.scalatest.{FunSuite, Matchers}
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Headers
import play.api.test.FakeRequest

import scala.io.Source

/**
  * Created by me on 31/07/2016.
  */
class TestPushParse extends FunSuite with Matchers {

  lazy val pingSample = TestPushParse.parseSample(scala.io.Source.fromURL(getClass.getResource("/sample-ping.txt")))
  lazy val pushSample = TestPushParse.parseSample(scala.io.Source.fromURL(getClass.getResource("/sample-push.txt")))

  test("Sample parses") {
    pingSample
    pushSample
    info(s"Push signature = ${pushSample.signature}")
    pushSample.repositoryName shouldBe "AptElements/git-watch"
  }

  test("Using a secret works") {
    pushSample.signatureValid("test").get shouldBe true
    pushSample.signatureValid("testX").get shouldBe false
    pushSample.requestId shouldBe "447f65f3-e3a0-43bb-8cd9-5e5a1cd8ae08"
  }

  test("Extracting the push works") {
    pushSample.eventType == PushEvent
    val pp = PushEvent.unapply(pushSample.bodyJson).get
    pp.ref shouldBe "refs/heads/master"
    pp.after shouldBe "1f07dd2f46445a7ba3cb6f6a938ba9e3b855362c"
  }

  test("Applying the 'deploy to x branch' preset works") {

  }

  test("Watcher seems to work") {
    val watcher = GitHub.buildWatcher("AptElements/git-watch", FakeRequest(
      method = "GET", uri = "/?event=simple-push", headers = Headers(), body = ""
    ))
    watcher.isRight shouldBe true
    val wrg = watcher.right.get
    val wrsp = wrg.apply(pushSample)
    val wrspv = wrsp.get
    wrspv.name.get shouldBe "ref-push"
    wrspv.data shouldBe """{"ref":"refs/heads/master","commit":"1f07dd2f46445a7ba3cb6f6a938ba9e3b855362c"}"""
    wrspv.id.get shouldBe "447f65f3-e3a0-43bb-8cd9-5e5a1cd8ae08"
  }

}

object TestPushParse {
  def parseSample(source: Source): HookRequest = {
    val headers = source.getLines().takeWhile(_ != "").map(_.split(": ")).map { case Array(h, v) =>
      h -> v
    }.toList.groupBy(_._1).mapValues(_.map(_._2))
    val body = source.getLines().next()
    val jsonBody = Json.parse(body).asInstanceOf[JsObject]
    HookRequest.extract(headers = headers, body = body, bodyJson = jsonBody).get
  }
}





