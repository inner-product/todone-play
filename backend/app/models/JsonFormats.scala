package models

import play.api.libs.json._
import todone.data._

object JsonFormats {
  implicit val idFormat: Format[Id] = Json.format[Id]

  implicit val projectFormat: Format[Project] = Json.format[Project]

  implicit val stateFormat: Format[State] = {
    import State._

    val readsState =
      (JsPath \ "Open")
        .read[State](Open)
        .orElse((JsPath \ "Closed").read[State](Closed))

    new Format[State] {
      def reads(json: JsValue): JsResult[State] =
        readsState.reads(json)

      def writes(o: State): JsValue =
        o match {
          case Open   => Json.obj("Open" -> Json.obj())
          case Closed => Json.obj("Closed" -> Json.obj())
        }
    }
  }

  implicit val tagFormat: Format[Tag] = Json.format[Tag]

  implicit val tagsFormat: Format[Tags] = Json.format[Tags]

  implicit val taskFormat: Format[Task] =
    Json.format[Task]

  implicit val tasksFormat: Format[Tasks] =
    Json.format[Tasks]
}
