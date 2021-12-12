package todone.data

import io.circe.Codec
import io.circe.generic.semiauto._

final case class Tasks(tasks: List[(Id, Task)])
object Tasks {
  val empty = Tasks(List.empty)

  implicit val tasksCodec: Codec[Tasks] = deriveCodec[Tasks]
}
