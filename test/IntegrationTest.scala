/**
  * Created by me on 17/08/2016.
  */

import org.scalatestplus.play._
import play.api.inject.guice.GuiceApplicationBuilder

class IntegrationTest extends PlaySpec with OneServerPerSuite with IntegrationTestBasic {
  implicit override lazy val app = new GuiceApplicationBuilder().build
}


