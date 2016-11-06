/**
  * Created by me on 17/08/2016.
  */

import controllers.github.EventServer
import model.github.{CompleteDataPush, RefPush}
import org.scalatestplus.play._
import play.api.libs.ws.{WSClient, WSResponse}
import play.api.test.Helpers._
import org.scalatest.Matchers._

import scala.concurrent.ExecutionContext
import Samples.pushSample

import scala.collection.mutable.ArrayBuffer

class IntegrationTest extends PlaySpec with OneServerPerSuite {

  "Full query" must {
    "Produce an output" in {
      val dataChunks = queryStream()
      dataChunks shouldBe empty
      sendPush().status mustBe OK
      Thread.sleep(100)
      val chunkString = dataChunks.mkString("")
      chunkString should (equal(expectedSiblingEvents) or equal(expectedKeepAliveWithSibling))
    }
  }

  implicit def mat = app.injector.instanceOf[akka.stream.Materializer]

  implicit def ec = app.injector.instanceOf[ExecutionContext]

  def sendPush(): WSResponse = {
    await {
      wsClient.url(url)
        .withHeaders(pushSample.rebuildHeaders.toList: _*)
        .post(pushSample.bodyJson)
    }
  }

  def queryStream(): ArrayBuffer[String] = {
    val results = scala.collection.mutable.ArrayBuffer.empty[String]
    val eventStream = await {
      wsClient
        .url(streamUrl)
        .withMethod("GET")
        .stream()
    }
    eventStream.body.runForeach { bs =>
      results += new String(bs.toArray)
    }
    results
  }

  def wsClient = app.injector.instanceOf[WSClient]

  def url = s"http://localhost:$port/github/"

  def streamUrl = s"http://localhost:$port/github/AptElements/git-watch"

  def expectedSiblingEvents = {
    val firstEvent = CompleteDataPush.buildEvent(pushSample).get.formatted
    val secondEvent = RefPush.buildEvent(pushSample).get.formatted
    s"$firstEvent$secondEvent"
  }

  def expectedKeepAliveWithSibling = s"${EventServer.keepAliveEvent.formatted}${expectedSiblingEvents}"

}
