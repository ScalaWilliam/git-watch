package controllers

import java.io.ByteArrayOutputStream
import java.nio.file.Path
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.{StreamResult, StreamSource}

/**
  * Created by me on 22/10/2016.
  */
object RenderXML {
  def apply(path: Path): String = {
    val tf = TransformerFactory.newInstance()
    val fileSource = new StreamSource(path.toFile)
    val bas = new ByteArrayOutputStream()
    val streamResult = new StreamResult(bas)
    val stylesheet = tf.getAssociatedStylesheet(fileSource, null, null, null)
    val transformerX = tf.newTransformer(stylesheet)
    transformerX.transform(fileSource, streamResult)
    val bytes = bas.toByteArray
    new String(bytes, "UTF-8")
  }
}
