package services

import javax.inject.{Inject, Singleton}

import org.apache.http.client.cache.HttpCacheContext
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.cache.CachingHttpClientBuilder
import org.apache.http.util.EntityUtils

/**
  * Created by me on 11/03/2017.
  */
@Singleton
class ReadmeService @Inject()() {
  private val client: CloseableHttpClient = CachingHttpClientBuilder.create().build()
  private val context = HttpCacheContext.create()
  private val readmeUrl = "https://api.github.com/repos/ScalaWilliam/git-watch/readme"
  private val acceptContentType = "application/vnd.github.v3.html"

  def htmlString(): String = {
    val httpGet = new HttpGet(readmeUrl)
    httpGet.setHeader("Accept", acceptContentType)
    EntityUtils.toString(client.execute(httpGet, context).getEntity)
  }
}
