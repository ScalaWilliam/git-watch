package lib

import java.nio.file.{Files, Path}
import javax.inject.Inject

import play.api.Environment

/**
  * Created by me on 11/02/2017.
  */
class ContentPath @Inject()(environment: Environment) {
  def contentPath: Path = {
    val rootPath = environment.rootPath.toPath
    val distContent = rootPath.resolve("dist").resolve("content")
    val content = rootPath.resolve("content")
    if (Files.exists(distContent)) distContent
    else if (Files.exists(content)) content
    else throw new IllegalArgumentException("Could not find content path.")
  }
}
