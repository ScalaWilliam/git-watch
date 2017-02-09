import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Headers
import play.api.test.FakeRequest

import scala.io.Source

/**
  * Created by me on 18/08/2016.
  */
object Samples {

  def fromRequest(source: => Source): FakeRequest[JsValue] = {
    val src = source.getLines().toList
    FakeRequest(
      method = src.head.split(" ")(0),
      uri = src.head.split(" ")(1),
      headers = Headers(src.drop(1).takeWhile(_ != "").map(_.split(": ")).map {
        case Array(hn, hv) => hn -> hv
      }: _*),
      body = Json.parse(src.last)
    )
  }


}
