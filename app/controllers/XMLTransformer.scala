package controllers

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, File, InputStream}
import java.nio.file.Path
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.{StreamResult, StreamSource}

/**
  * Render an XML document with a stylesheet specified in its body.
  */
case class XMLTransformer(transformerFactory: TransformerFactory) {
  def transform(xmlPath: Path): String = {
    val fileSource = new StreamSource(xmlPath.toFile)
    val baos = new ByteArrayOutputStream()
    val streamResult = new StreamResult(baos)
    val stylesheet = transformerFactory.getAssociatedStylesheet(fileSource, null, null, null)
    val transformer = transformerFactory.newTransformer(stylesheet)
    transformer.transform(fileSource, streamResult)
    val bytes = baos.toByteArray
    new String(bytes, "UTF-8")
  }

  protected def createFileSource(xmlString: String, systemId: File): (StreamSource, InputStream) = {
    val is = new ByteArrayInputStream(xmlString.getBytes("UTF-8"))
    val src = new StreamSource(is)
    src.setSystemId(systemId)
    (src, is)
  }

  def transform(xmlString: String, rootPath: Path): String = {
    val (streamSource1, inputStream1) = createFileSource(xmlString, rootPath.toFile)
    val (streamSource2, inputStream2) = createFileSource(xmlString, rootPath.toFile)
    try {
      val bas = new ByteArrayOutputStream()
      val streamResult = new StreamResult(bas)
      val stylesheet = transformerFactory.getAssociatedStylesheet(streamSource1, null, null, null)
      val transformer = transformerFactory.newTransformer(stylesheet)
      transformer.transform(streamSource2, streamResult)
      val bytes = bas.toByteArray
      new String(bytes, "UTF-8")
    } finally {
      inputStream1.close()
      inputStream2.close()
    }
  }
}

object XMLTransformer extends XMLTransformer(TransformerFactory.newInstance())
