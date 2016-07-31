package controllers

import javax.inject.{Inject, Singleton}

import play.api.mvc.Controller

/**
  * Created by me on 31/07/2016.
  */

@Singleton
class GitHub @Inject()() extends Controller {
  def push(id: String) = TODO

  def get(id: String) = TODO
}
