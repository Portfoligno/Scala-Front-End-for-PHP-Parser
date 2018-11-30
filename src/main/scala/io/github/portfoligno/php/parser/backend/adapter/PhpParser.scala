package io.github.portfoligno.php.parser.backend.adapter

import cats.effect.{ConcurrentEffect, Resource}
import fs2.Stream
import io.github.portfoligno.php.parser.backend.model.Node
import io.circe.fs2._

import scala.concurrent.ExecutionContext

trait PhpParser[F[_]] {
  def parse(mode: ParserFactoryMode, source: String): Stream[F, Node]
}

object PhpParser {
  def resource[F[_] : ConcurrentEffect](
    implicit executionContext: ExecutionContext
  ): Resource[F, PhpParser[F]] =
    PhpToJsonParser
      .resource[F]
      .map(p => p
        .parse(_, _)
        .through(byteArrayParser)
        .through(decoder[F, Node]))
}
