/**
  * Created by me on 17/08/2016.
  */

import controllers.github.EventServer
import model.github.{CompleteDataPush, RefPush}
import org.scalatestplus.play._
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.ws.WSClient
import play.api.test.Helpers._
import org.scalatest.Matchers._

import scala.concurrent.ExecutionContext

class IntegrationTest extends PlaySpec with OneServerPerSuite {
  implicit override lazy val app = new GuiceApplicationBuilder().build

  import Samples.pushSample

  "Full query" must {
    "Produce an output" in {
      val wsClient = app.injector.instanceOf[WSClient]
      implicit val mat = app.injector.instanceOf[akka.stream.Materializer]
      implicit val ec = app.injector.instanceOf[ExecutionContext]
      val url = s"http://localhost:$port/github/"
      val streamUrl = s"http://localhost:$port/github/AptElements/git-watch"
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
      results shouldBe empty
      val response = await {
        wsClient.url(url)
          .withHeaders(pushSample.rebuildHeaders.toList: _*)
          .post(pushSample.bodyJson)
      }
      response.status mustBe OK
      Thread.sleep(100)
      val result = results.mkString("")
      val A = CompleteDataPush.buildEvent(pushSample).get.formatted
      val B = RefPush.buildEvent(pushSample).get.formatted
      result should (equal (s"$A$B") or equal (s"${EventServer.keepAliveEvent.formatted}$A$B"))
    }
  }
}


