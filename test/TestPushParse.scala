import controllers.GitHub.PushParse
import org.scalatest.{FunSuite, Matchers}
import play.api.libs.json.Json

/**
  * Created by me on 31/07/2016.
  */
class TestPushParse extends FunSuite with Matchers {

  test("It works") {
    val sampleJson = """{"ref":"refs/heads/master","before":"c04b0fb8d488e198eab72dc1ff4e619deb30a284","after":"a8b8c587f7dc2750328530b3a0434edeedffca83","created":false,"deleted"}"""
    val json = Json.parse(sampleJson)
    json.as[PushParse].ref shouldBe "refs/heads/master"
  }

  test("Using a secret works") {

  }

  test("Applying the 'deploy to x branch' preset works") {

  }

}
