package io.github.portfoligno.php.parser.backend.model

import io.circe.{Decoder, DecodingFailure, Encoder, Json}

import scala.reflect.ClassTag

private[model]
class OrdinalConverter[A](implicit A: ClassTag[A]) {
  def toOrdinal(value: A): Int =
    value.getClass.getSimpleName.drop(1).dropRight(1).toInt

  def fromOrdinal(ordinal: Int): Option[A] = {
    val m = scala.reflect.runtime.universe.runtimeMirror(getClass.getClassLoader)

    try {
      A.unapply(m.reflectModule(m.staticModule(s"${A.runtimeClass.getName}$$_$ordinal$$")).instance)
    }
    catch {
      case _: ScalaReflectionException =>
        None
    }
  }

  implicit lazy val encoder: Encoder[A] =
    value => Json.fromInt(toOrdinal(value))

  implicit lazy val decoder: Decoder[A] =
    c => c
      .value
      .asNumber
      .fold[Option[A]](None)(_.toInt.flatMap(fromOrdinal))
      .toRight(DecodingFailure(A.runtimeClass.getSimpleName, c.history))
}
