package controllers

import java.nio.file.Paths

import lib.XMLTransformer
import org.scalatest.FunSuite
import org.scalatest.Matchers._

class RenderXMLDocumentTest extends FunSuite {
  def idRender = """<?xml-stylesheet type="text/xsl" href="include.xsl"?>"""

  test("it works") {
    val result = XMLTransformer.transform(
      xmlString = idRender + "<exclude><include/></exclude>",
      rootPath = Paths.get(getClass.getResource("/").toURI)
    )
    result shouldEqual """<?xml version="1.0" encoding="UTF-8"?><include/>"""
  }

}
