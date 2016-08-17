/**
  * Created by me on 17/08/2016.
  */

import controllers.GitHub
import org.scalatestplus.play._
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.ws.WSClient
import play.api.mvc._
import play.api.test._
import play.api.test.Helpers._

import scala.concurrent.Future

class IntegrationTest extends PlaySpec with OneServerPerSuite with Results {

  import Samples.pushSample

  implicit override lazy val app = new GuiceApplicationBuilder()
    .build
  "Example Page#index" should {
    "should be valid" in {
      val wsClient = app.injector.instanceOf[WSClient]
      val url = s"http://localhost:$port/github/"

      val response = await {
        wsClient.url(url)
          .withHeaders(pushSample.rebuildHeaders.toList: _*)
          .post(pushSample.bodyJson)
      }
      response.status mustBe OK
    }
  }
}

