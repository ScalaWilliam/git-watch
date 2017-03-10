import org.scalatest.FunSuite
import play.core.server.common.SubnetValidate

/**
  * Created by me on 11/03/2017.
  */
class IpValidateTest extends FunSuite {
  test("IP outside of range does not pass") {
    assert(!SubnetValidate.validate("192.30.252.0/22", "192.31.252.0"))
  }
  test("IP inside range does pass") {
    assert(SubnetValidate.validate("192.30.252.0/22", "192.30.252.0"))
  }
}
