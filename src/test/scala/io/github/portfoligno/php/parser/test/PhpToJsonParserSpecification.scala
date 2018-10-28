package io.github.portfoligno.php.parser.test

import cats.effect.internals.IOContextShift
import cats.effect.{ContextShift, IO, Resource}
import cats.instances.string._
import cats.syntax.flatMap._
import fs2.text.utf8Decode
import io.github.portfoligno.php.parser.backend.adapter.{ParserFactoryMode, PhpToJsonParser}
import org.scalacheck.Prop._
import org.scalacheck.Properties

object PhpToJsonParserSpecification extends Properties(classOf[PhpToJsonParser[IO]].getSimpleName) {
  import scala.concurrent.ExecutionContext.Implicits._
  private
  implicit def contextSwitch: ContextShift[IO] = IOContextShift.global

  private
  val parser = PhpToJsonParser.resource[IO]

  property("parse") = delay {
    val s = parser
      .use(_
        .parse(ParserFactoryMode.PREFER_PHP7, "")
        .map(_
          .through(utf8Decode)
          .compile
          .foldMonoid)
        .flatten)
      .unsafeRunSync()
    log.info(s)

    s != null
  }
}
