package todone.data

import io.circe.Codec
import io.circe.generic.semiauto._

sealed trait State
object State {
  case object Open extends State
  case object Closed extends State

  val open: State = Open
  val closed: State = Closed

  implicit val stateCodec: Codec[State] = deriveCodec[State]
}
