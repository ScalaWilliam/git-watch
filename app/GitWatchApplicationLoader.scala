import play.api.ApplicationLoader.Context
import play.api._
import play.api.mvc.EssentialFilter
import play.filters.HttpFiltersComponents
import router.Routes

class GitWatchApplicationLoader extends ApplicationLoader {
  def load(context: Context): Application =
    new GitWatchComponents(context).application
}

class GitWatchComponents(context: Context)
    extends BuiltInComponentsFromContext(context)
    with HttpFiltersComponents
    with _root_.controllers.AssetsComponents {

  override def httpFilters: Seq[EssentialFilter] =
    Seq(csrfFilter, securityHeadersFilter)

  lazy val eventServer = new _root_.controllers.EventServer(configuration)(actorSystem,
                                                                           controllerComponents,
                                                                           executionContext)

  lazy val router = new Routes(httpErrorHandler, assets, eventServer)

}
