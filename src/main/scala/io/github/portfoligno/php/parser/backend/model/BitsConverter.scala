package io.github.portfoligno.php.parser.backend.model

import io.circe.{Decoder, DecodingFailure, Encoder, Json}

import scala.reflect.ClassTag

private[model]
class BitsConverter[A](implicit A: ClassTag[A]) {
  private
  val ordinals = new OrdinalConverter[A]

  def toBits(a: Set[A]): Long =
    a.foldLeft(0L)((sum, a) => sum + (1 << ordinals.toOrdinal(a)))

  def fromBits(bits: Long)(implicit A: ClassTag[A]): Set[A] =
    Stream
      .iterate(bits)(_ >>> 1)
      .takeWhile(_ != 0)
      .map(i => i & 1)
      .zipWithIndex
      .collect {
        case (1L, shifts) =>
          ordinals.fromOrdinal(shifts)
      }
      .flatten
      .toSet

  implicit lazy val setEncoder: Encoder[Set[A]] =
    set => Json.fromLong(toBits(set))

  implicit lazy val setDecoder: Decoder[Set[A]] =
    c => c
      .value
      .asNumber
      .flatMap(_.toLong)
      .map(fromBits)
      .toRight(DecodingFailure(s"Set[${A.runtimeClass.getSimpleName}]", c.history))
}
