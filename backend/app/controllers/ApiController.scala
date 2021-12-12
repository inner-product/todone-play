package controllers

import javax.inject._

import models._
import play.api.libs.json.Json
import play.api.mvc._
import todone.data._

@Singleton
class ApiController @Inject() (
    val controllerComponents: ControllerComponents
) extends BaseController {
  import JsonFormats._

  def tasks() = Action { implicit request: Request[AnyContent] =>
    Ok(Json.toJson(DefaultTasks.defaultTasks))
  }
}
