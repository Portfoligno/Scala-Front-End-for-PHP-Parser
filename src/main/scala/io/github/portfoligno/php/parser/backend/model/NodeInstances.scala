package io.github.portfoligno.php.parser.backend.model

import io.circe.generic.extras.Configuration
import io.circe.{Decoder, Encoder, Json}

import scala.util.matching.Regex

private[model] class NodeInstances {
  private
  val packageRegex = raw"""^${Regex.quoteReplacement(classOf[Node].getName)}\.""".r
  private
  val singletonTypeRegex = raw"""\.type$$""".r

  implicit val configuration: Configuration = Configuration
    .default
    .withDiscriminator("nodeType")
    .copy(
      transformConstructorNames = _
        .replaceFirst(packageRegex, "")
        .replaceFirst(singletonTypeRegex, "")
        .replace('.', '_'))

  implicit def eitherEncoder[N <: Node](implicit nodeEncoder: Encoder[N]): Encoder[Either[String, N]] =
    _.fold(Json.fromString, nodeEncoder.apply)

  implicit def eitherDecoder[N <: Node](implicit nodeDecoder: Decoder[N]): Decoder[Either[String, N]] =
    Decoder.decodeString.either(nodeDecoder)
}
