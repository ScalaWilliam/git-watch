package controllers

import javax.inject.{Inject, Singleton}

import lib.ContentPath
import org.jsoup.Jsoup
import play.api.mvc.{Action, Controller}
import play.twirl.api.Html
import services.ReadmeService

/**
  * Created by me on 18/08/2016.
  */
@Singleton
class Index @Inject()(contentPath: ContentPath, readmeService: ReadmeService) extends Controller {

  def index = Action {
    val document = Jsoup.parse(contentPath.contentPath.resolve("index.html").toFile, "UTF-8")
    document.select("#readme").html(readmeService.htmlString())
    val githubButton = document.select("#git-stars-button").first()
    document.select("#readme h1").first().appendText(" ")
    document.select("#readme h1").first().appendChild(githubButton.clone())
    githubButton.remove()
    Ok(Html(document.outerHtml()))
  }

}
