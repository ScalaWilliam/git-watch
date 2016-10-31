package services.github

/**
  * Created by me on 22/10/2016.
  */

import play.api.{Configuration, Environment, Mode}
import play.api.inject._

class InstallationChoice extends Module {
  def bindings(environment: Environment,
               configuration: Configuration) = Seq(
    if (environment.mode == Mode.Prod) bind[Installation].to[RealInstallation]
    else bind[Installation].to[StubInstallation]
  )
}
