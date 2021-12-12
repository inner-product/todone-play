package todone.data

import io.circe.Codec
import io.circe.generic.semiauto._

final case class Project(name: String)
object Project {
  implicit val projectCodec: Codec[Project] = deriveCodec[Project]
}
