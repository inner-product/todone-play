package todone.data

import io.circe.Codec
import io.circe.generic.semiauto._

final case class Projects(projects: List[Project])
object Projects {
  val empty = Projects(List.empty)

  implicit val projectsCodec: Codec[Projects] = deriveCodec[Projects]
}
