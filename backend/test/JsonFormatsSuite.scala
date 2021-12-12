package todone

import io.circe._
import io.circe.syntax._
import models.{DefaultTasks, JsonFormats}
import munit.FunSuite
import play.api.libs.json.{Json => PlayJson, _}
import todone.data._

class JsonFormatsSuite extends FunSuite {
  def circeWrite[A: Encoder](a: A): String =
    a.asJson.spaces2

  def circeRead[A: Decoder](s: String): A =
    io.circe.parser.decode[A](s) match {
      case Left(error)  => throw new Exception(error)
      case Right(value) => value
    }

  def playRead[A](s: String)(implicit r: Reads[A]): A =
    r.reads(PlayJson.parse(s))
      .fold(
        invalid = errors => throw new Exception(errors.toString()),
        valid = identity
      )

  def playWrite[A](a: A)(implicit w: Writes[A]): String =
    PlayJson.stringify(w.writes(a))

  def testBijection[A](name: String, a: A)(implicit
      loc: munit.Location,
      c: Codec[A],
      f: Format[A]
  ): Unit =
    test(s"$name is a bijection") {
      assertEquals(playRead[A](circeWrite(a)), a)
      assertEquals(circeRead[A](playWrite(a)), a)
    }

  import JsonFormats._

  testBijection("Id", Id(1))
  testBijection("Tasks", DefaultTasks.defaultTasks)
}
