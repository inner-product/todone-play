package todone.data

import io.circe.Codec
import io.circe.generic.semiauto._

final case class Id(id: Int)
object Id {
  implicit val idCodec: Codec[Id] = deriveCodec[Id]
}
