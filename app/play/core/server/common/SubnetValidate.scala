package play.core.server.common

import java.net.InetAddress

/**
  * Created by me on 11/03/2017.
  */
object SubnetValidate {
  def validate(subnet: String, ip: InetAddress): Boolean =
    Subnet(subnet).isInRange(ip)

  def validate(subnet: String, ip: String): Boolean =
    validate(subnet, InetAddress.getByName(ip))
}
