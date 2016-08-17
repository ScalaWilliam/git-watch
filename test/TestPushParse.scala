
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

  import Samples.pushSample

  test("Using a secret works") {
    pushSample.signatureValid("test").get shouldBe true
    pushSample.signatureValid("testX").get shouldBe false
    pushSample.requestId shouldBe "56fd4d80-646e-11e6-9eff-715f55605d90"
  }

  test("Extracting the push works") {
    pushSample.eventType == PushEvent
    val pp = PushEvent.unapply(pushSample.bodyJson).get
    pp.ref shouldBe "refs/heads/master"
    pp.after shouldBe "1f07dd2f46445a7ba3cb6f6a938ba9e3b855362c"
  }

  test("Watcher produces a ref event and a complete event") {
    val watcher = GitHub.buildWatcher("AptElements/git-watch", FakeRequest(
      method = "GET", uri = "/?event=simple-push", headers = Headers(), body = ""
    ))

    val List(first, second) = watcher.apply(pushSample)

    first.name.get shouldBe "push"
    first.data shouldBe pushSample.body
    first.id.get shouldBe "56fd4d80-646e-11e6-9eff-715f55605d90"

    second.name.get shouldBe "ref-push"
    second.data shouldBe """{"ref":"refs/heads/master","commit":"1f07dd2f46445a7ba3cb6f6a938ba9e3b855362c"}"""
    second.id.get shouldBe "56fd4d80-646e-11e6-9eff-715f55605d90"
  }

  test("Watcher secret fails") {
    val watcher = GitHub.buildWatcher("AptElements/git-watch", FakeRequest(
      method = "GET", uri = "/?event=simple-push&secret=a", headers = Headers(), body = ""
    ))
    watcher(pushSample) shouldBe empty
  }

  test("Watcher secret works") {
    val watcher = GitHub.buildWatcher("AptElements/git-watch", FakeRequest(
      method = "GET", uri = "/?event=simple-push&secret=test", headers = Headers(), body = ""
    ))
    watcher(pushSample) should have size 2
  }

}

object TestPushParse {
}





