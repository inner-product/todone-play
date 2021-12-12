package todone.data

import io.circe.Codec
import io.circe.generic.semiauto._

final case class Tag(name: String)
object Tag {
  implicit val tagCodec: Codec[Tag] = deriveCodec[Tag]
}
