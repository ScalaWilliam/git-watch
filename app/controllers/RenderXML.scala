package controllers

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
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

  def apply(data: String, root: Path): String = {
    val tf = TransformerFactory.newInstance()

    def xfileSource = {
      val is = new ByteArrayInputStream(data.getBytes("UTF-8"))
      val src = new StreamSource(is)
      src.setSystemId(root.toFile)
      (src, is)
    }
    val (fs1, is1) = xfileSource
    val (fs2, is2) = xfileSource
    try {
      val bas = new ByteArrayOutputStream()
      val streamResult = new StreamResult(bas)
      val stylesheet = tf.getAssociatedStylesheet(fs1, null, null, null)
      val transformerX = tf.newTransformer(stylesheet)
      transformerX.transform(fs2, streamResult)
      val bytes = bas.toByteArray
      new String(bytes, "UTF-8")
    } finally {
      is1.close()
      is2.close()
    }
  }
}
