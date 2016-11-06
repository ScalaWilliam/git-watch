import java.net.URL

import model.github.HookRequest
import org.scalatest.FunSuite
import org.scalatest.Matchers._
import play.api.libs.json.{JsObject, Json}

import scala.io.Source

/**
  * Created by me on 18/08/2016.
  */
class SampleTest extends FunSuite {

  import Samples.pushSample

  test("Sample parses") {
    pushSample
    info(s"Push signature = ${pushSample.signature}")
    pushSample.repositoryName shouldBe "AptElements/git-watch"
  }

  test("Sample rebuilds") {
    val result = HookRequest.extract(
      headers = pushSample.rebuildHeaders.mapValues(v => List(v)),
      body = pushSample.body,
      bodyJson = pushSample.bodyJson
    ).get
    result shouldEqual pushSample
  }
}

