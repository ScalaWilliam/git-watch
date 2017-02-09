import model.github.PushEvent$
import org.scalatest.FunSuite
import org.scalatest.Matchers._
import org.scalatest.OptionValues._

import scala.io.Source

/**
  * Created by me on 18/08/2016.
  */
class SampleTest extends FunSuite {

  testPlatform(
    platformName = "GitHub",
    expectedUrl = "https://github.com/AptElements/git-watch"
  )

  testPlatform(
    platformName = "BitBucket",
    expectedUrl = "https://bitbucket.org/ScalaWilliam/weh"
  )

  testPlatform(
    platformName = "GitLab",
    expectedUrl = "https://gitlab.com/ScalaWilliam/test-project"
  )

  def testPlatform(platformName: String, expectedUrl: String) = {
    test(s"${platformName} sample works") {
      val resource = s"/${platformName.toLowerCase}-push.request"
      withClue(s"Resource: ${resource}") {
        PushEvent
          .AtRequest {
            Samples.fromRequest(Source.fromURL(getClass.getResource(resource)))
          }
          .anyEvent
          .value
          .repositoryUrl shouldEqual expectedUrl
      }
    }
  }

}

